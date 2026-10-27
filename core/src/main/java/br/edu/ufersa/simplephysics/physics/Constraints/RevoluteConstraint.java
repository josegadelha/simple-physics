package br.edu.ufersa.simplephysics.physics.Constraints;

import br.edu.ufersa.simplephysics.math.Mat2x2;
import br.edu.ufersa.simplephysics.math.SoftParams;
import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsObject;

public class RevoluteConstraint extends Constraint {

    //Variáveis que vão ser usadas
    public double beta;
    public Vector2 worldA, worldB;
    public double[][] K = {{0, 0}, {0, 0}};

    //rA e rB = vetores raio, ponto que vai do centro de massa do corpo até
    // o ponto de ancoragem
    public Vector2 rB, rA = new Vector2(0, 0);
    public Vector2 localA, localB;

    //invMass = massas inversas
    //invI = inércia inversa
    //Se um corpo for estático, sua massa e inércia = 0
    public double invMassA, invMassB, invIA, invIB;

    //Limite de ângulos inferior e superior, erro de ângulo e ângulo atual
    public Double lowerAngleLimit, upperAngleLimit, angleViolation, currentAngle;

    //Rigidez
    public double stiffness;

    //Vetor de impulsos acumulados
    public Vector2 accumulatedPointImpulse = new Vector2(0, 0);

    //Construtor da classe
    public RevoluteConstraint(PhysicsObject bodyA, PhysicsObject bodyB, Vector2 worldPoint, Double lowerAngleLimit, Double upperAngleLimit, double stiffness) {
        super(bodyA, bodyB);
        //WorldA, WorldB, WorldPoint = Pontos das juntas entre os corpos A e B
        this.localA = bodyA.worldToLocal(worldPoint);
        this.localB = bodyB.worldToLocal(worldPoint);

        //Limites angulares inferior e superior
        this.lowerAngleLimit = lowerAngleLimit;
        this.upperAngleLimit = upperAngleLimit;

        //Rigidez
        this.stiffness = stiffness;

        //Inicializa os valores das massas e inércias dos corpos A e B
        this.invMassA = bodyA.isStatic ? 0 : 1.0 / bodyA.mass;
        this.invMassB = bodyB.isStatic ? 0 : 1.0 / bodyB.mass;
        this.invIA = bodyA.isStatic ? 0 : 1.0 / bodyA.momentOfInertia;
        this.invIB = bodyB.isStatic ? 0 : 1.0 / bodyB.momentOfInertia;
    }

    //Método que atualiza as posições dos pontos de ancoragem dos dois corpos
    //Sobrescreve o update da Constraint (Será usado em outros constraints)
    @Override
    public void update(Object settings){
        //Pega o ponto local fixo guardado em localA/B e o converte para o espaço
        //global (worldA/B), considerando a posição e rotação atuais do Corpo.
        this.worldA = this.bodyA.localToWorld(this.localA);
        this.worldB = this.bodyB.localToWorld(this.localB);

        //Calcula o vetor raio rA/B que vai do centro de massa do Corpo até o
        //ponto de ancoragem no mundo (worldA/B).
        //Esse vetor age como um braço de alavanca para calcular o torque.
        this.rA = this.worldA.subtract(this.bodyA.position);
        this.rB = this.worldB.subtract(this.bodyB.position);

        //Calcula ângulo atual, rotação relativa entre os corpos
        this.currentAngle = this.bodyB.rotation - this.bodyA.rotation;

        //Atualiza os valores de massa inversa (invMass)
        //e inércia rotacional inversa (invI) para ambos os corpos.
        //Se um corpo virar estático (isStatic == true),
        //esses valores tornam-se 0,
        //impedindo que ele seja movido por forças da junta.
        this.invMassA = this.bodyA.isStatic ? 0 : 1.0 / this.bodyA.mass;
        this.invMassB = this.bodyB.isStatic ? 0 : 1.0 / this.bodyB.mass;
        this.invIA = this.bodyA.isStatic ? 0 : 1.0 / this.bodyA.momentOfInertia;
        this.invIB = this.bodyB.isStatic ? 0 : 1.0 / this.bodyB.momentOfInertia;

        //Verifica se esta tem uma configuração local (this.constraintSettings).
        //Se não tiver, ela herda e utiliza as configurações globais
        //do mundo repassadas no parâmetro settings.
        ConstraintSettingsModel cs = (this.constraintSettings != null) ? (ConstraintSettingsModel) this.constraintSettings : (ConstraintSettingsModel) settings;

        //Warm Starting: Reaplica o impulso do frame anterior para acelerar a convergência
        if (cs == null || cs.warmStarting) {
            this.bodyA.applyImpulse(this.accumulatedPointImpulse.scale(-1), this.worldA);
            this.bodyB.applyImpulse(this.accumulatedPointImpulse, this.worldB);
        }

        //Se houver um limite inferior e o ângulo atual for menor, calcula o tamanho do erro
        //Se houver um limite superior e o ângulo atual for maior, calcula erro de excesso
        //Se o ângulo estiver dentro dos limites, zera o erro de ângulo
        if (this.lowerAngleLimit != null && this.currentAngle < this.lowerAngleLimit) {
            this.angleViolation = this.currentAngle - this.lowerAngleLimit;
        } else if (this.upperAngleLimit != null && this.currentAngle > this.upperAngleLimit) {
            this.angleViolation = this.currentAngle - this.upperAngleLimit;
        } else {
            this.angleViolation = 0.0;
        }
    }

    //Método que corrige erros de velocidade, posição, motores e limites angulares
    @Override
    public void solve(double dt, Object settings){
        //Se não tiver configuração já feita, usa a configuração global
        ConstraintSettingsModel cs = (this.constraintSettings != null) ? (ConstraintSettingsModel) this.constraintSettings : (ConstraintSettingsModel) settings;

        //Mantém os pontos de ancoragem colados
        solvePointConstraint(dt, cs);

        //MÉTODOS QUE AINDA NÃO EXISTEM ABAIXO!!
        //Aplica força de rotação se um motor estiver ativo
        solveMotor(dt);

        //Verifica se rotação entre os corpos ultrapassou os limites angulares
        solveAngleLimits(dt, cs);
    }

    public void solvePointConstraint(double dt, ConstraintSettingsModel cs){
        //Elementos da matriz K (Matriz de massa efetiva)
        //Massas dos corpos A e B
        double mA = this.invMassA;
        double mB = this.invMassB;

        //Impulsos de A e B
        double iA = this.invIA;
        double iB = this.invIB;

        //Raios de A e B
        Vector2 rA = this.rA;
        Vector2 rB = this.rB;

        //Montagem da matriz K (relaciona impulso e alteração de velocidade)
        this.K[0][0] = mA + mB + rA.y * rA.y * iA + rB.y * rB.y * iB;
        this.K[0][1] = -rA.y * rA.x * iA - rB.y * rB.x * iB;
        this.K[1][0] = this.K[0][1];
        this.K[1][1] = mA + mB + rA.x * rA.x * iA + rB.x * rB.x * iB;

        //Calcula velocidade linear: velocidade do centro de massa + velocidade da rotação
        // * raio
        Vector2 velA = this.bodyA.velocity.add(rA.crossProductScalarVector(this.bodyA.angularVelocity));
        Vector2 velB = this.bodyB.velocity.add(rB.crossProductScalarVector(this.bodyB.angularVelocity));

        //O objetivo dos dois erros abaixo (C' e C) é permanecer zero para garantir que
        //os dois corpos em questão estejam colados
        //Erro de velocidade: Velocidade do ponto A - velocidade do ponto B
        Vector2 Cdot = velB.subtract(velA);

        //Erro de posição: distância entre o ponto A e B
        Vector2 C = this.worldB.subtract(this.worldA);

        //Variáveis que vão corrigir o erro de posição, utiliza Estabilização
        //de Baumgarte ou Soft, depende do modo escolhido
        Vector2 bias = new Vector2(0, 0);
        double massScale = 1.0;
        double impulseScale = 0.0;

        //Se for pelo Baumgarte, o bias calcula a divisão entre o erro de posição
        //e o delta passado no parâmetro
        if("baumgarte".equals(cs.mode)){
            bias = C.scale(cs.baumgarteFactor / dt);
        }
        //Se for pelo Soft, a frequência máxima é limitada para evitar
        //instabilidade, os parâmetros de amortecedor/mola são obtidos e
        //as variáveis de correção são atualizadas
        else if ("soft".equals(cs.mode)) {
            double maxHertz = 0.25 / dt;
            double hz = Math.min(cs.jointSoft.hertz, maxHertz);
            SoftParams soft = SoftParams.getSoftConstraintParams(hz, cs.jointSoft.dampingRatio, dt);
            bias = C.scale(soft.biasRate);
            massScale = soft.massScale;
            impulseScale = soft.impulseScale;
        }

        //Usa a regra de Cramer para resolver a matriz que servirá como
        //vetor de impulso que zera os erros de velocidade e posição
        Vector2 impulse = Mat2x2.mat2x2Solve(this.K, Cdot.add(bias).scale(-1));

        //Modifica o vetor de impulso acumulado
        if ("soft".equals(cs.mode)) {
            impulse.x = massScale * impulse.x - impulseScale * this.accumulatedPointImpulse.x;
            impulse.y = massScale * impulse.y - impulseScale * this.accumulatedPointImpulse.y;
        }

        //Aplica o impulso nos corpos
        //3° Lei de Newton: o impulso no corpo A é negativo porque é uma
        //força oposta ao corpo B
        this.bodyA.applyImpulse(impulse.scale(-1), this.worldA);
        this.bodyB.applyImpulse(impulse, this.worldB);

        //Se tiver Warm Starting, acumula o impulso no vetor de impulso acumulado
        if (cs.warmStarting) {
            this.accumulatedPointImpulse = this.accumulatedPointImpulse.add(impulse);
        }
    }
}
