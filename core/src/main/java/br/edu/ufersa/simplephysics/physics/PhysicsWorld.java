
package br.edu.ufersa.simplephysics.physics;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.collision.CollisionData;
import br.edu.ufersa.simplephysics.physics.collision.CollisionHelper;
import br.edu.ufersa.simplephysics.physics.constraints.ContactConstraint;
import br.edu.ufersa.simplephysics.physics.shape.CircleShape;
import br.edu.ufersa.simplephysics.physics.shape.ConvexPolygonShape;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PhysicsWorld {

    public static final double MAX_OBJECT_DISTANCE = 1000.0;

    private static final int SOLVER_ITERATIONS = 10;

    // Número de tentativas para corrigir a sobreposição dos corpos.
    private static final int POSITION_ITERATIONS = 5;

    // Fração da penetração corrigida em cada tentativa.
    private static final double POSITION_CORRECTION = 0.2;

    // Pequena sobreposição tolerada para evitar correções desnecessárias.
    private static final double POSITION_SLOP = 0.005;

    // Ativa a impressão dos impulsos durante os testes.
    public boolean debugContactConstraints = false;

    // Guarda os impulsos dos contatos do quadro anterior.
    private Map<ContactKey, CachedContact> contactCache = new HashMap<>();

    public List<PhysicsObject> objects;
    public Vector2 gravity;

    // Colisões detectadas na última atualização do mundo.
    public List<CollisionData> collisions;

    public PhysicsWorld() {
        this.objects = new ArrayList<>();
        this.gravity = new Vector2(0, -9.81);
        this.collisions = new ArrayList<>();
    }

    public void addSquare(
            double x, double y, double sideLength, boolean isStatic) {

        if (!Double.isFinite(sideLength) || sideLength <= 0) {
            throw new IllegalArgumentException(
                "Square side length must be finite and positive"
            );
        }

        double halfSideLength = sideLength / 2;
        double mass = sideLength * sideLength;
        double momentOfInertia = mass * sideLength * sideLength / 6;

        PhysicsObject square = new PhysicsObject(
            x,
            y,
            isStatic,
            mass,
            momentOfInertia,
            new ConvexPolygonShape(
                new Vector2(-halfSideLength, -halfSideLength),
                new Vector2(halfSideLength, -halfSideLength),
                new Vector2(halfSideLength, halfSideLength),
                new Vector2(-halfSideLength, halfSideLength)
            )
        );

        objects.add(square);
    }

    public void addCircle(
            double x, double y, double radius, boolean isStatic) {

        if (!Double.isFinite(radius) || radius <= 0) {
            throw new IllegalArgumentException(
                "Circle radius must be finite and positive"
            );
        }

        double mass = Math.PI * radius * radius;
        double momentOfInertia = mass * radius * radius / 2;

        PhysicsObject circle = new PhysicsObject(
            x,
            y,
            isStatic,
            mass,
            momentOfInertia,
            new CircleShape(radius)
        );

        objects.add(circle);
    }

    public void step(double dt) {

        if (!Double.isFinite(dt) || dt < 0) {
            throw new IllegalArgumentException(
                "Time step must be finite and non-negative"
            );
        }

        // Aplica a gravidade somente aos objetos dinâmicos.
        for (PhysicsObject object : objects) {
            if (!object.isStatic) {
                object.velocity =
                    object.velocity.add(gravity.scale(dt));
            }
        }

        // Atualiza a posição e a rotação dos objetos.
        for (PhysicsObject object : objects) {
            object.step(dt);
        }

        // Remove objetos que estão muito distantes da origem.
        objects.removeIf(object ->
            Math.hypot(object.position.x, object.position.y)
                > MAX_OBJECT_DISTANCE
        );

        // Detecta as colisões após a atualização das posições.
        detectCollisions();

        // Resolve as velocidades dos corpos em contato.
        solveContactConstraints(dt);

        // Corrige as posições dos corpos que estão sobrepostos.
        correctPositions();

        // Atualiza as colisões para refletir as posições corrigidas.
        detectCollisions();
    }

    /**
     * Verifica todos os pares de objetos do mundo.
     * Armazena os dados das colisões encontradas.
     */
    public void detectCollisions() {

        // Descarta os resultados da atualização anterior.
        collisions.clear();

        // Cada par é analisado apenas uma vez.
        for (int i = 0; i < objects.size(); i++) {

            PhysicsObject bodyA = objects.get(i);

            for (int j = i + 1; j < objects.size(); j++) {

                PhysicsObject bodyB = objects.get(j);

                CollisionData collision =
                    CollisionHelper.checkCollision(bodyA, bodyB);

                if (collision != null) {
                    collisions.add(collision);
                }
            }
        }
    }


    /**
     * Cria e resolve as restrições de contato das colisões atuais,
     * reaproveitando os impulsos dos contatos do quadro anterior.
     */
    private void solveContactConstraints(double dt) {

        List<ContactConstraint> contactConstraints = new ArrayList<>();
        List<ContactKey> contactKeys = new ArrayList<>();

        // O novo cache conterá somente os contatos deste quadro.
        Map<ContactKey, CachedContact> nextCache = new HashMap<>();

        // Cria uma restrição para cada colisão detectada.
        for (CollisionData collision : collisions) {

            ContactKey key = new ContactKey(
                collision.bodyA,
                collision.bodyB
            );

            CachedContact cached = null;

            double previousNormalLambda = 0;
            double previousFrictionLambda = 0;

            if (cached != null) {

                double normalLength = collision.normal.length();

                if (normalLength > 1e-6) {
                    Vector2 currentNormal =
                        collision.normal.scale(1.0 / normalLength);

                    // Só reutiliza os impulsos se a normal não tiver
                    // mudado significativamente entre os quadros.

                    if (cached.normal.dotProduct(currentNormal) >= 0.9) {
                        previousNormalLambda = cached.normalLambda;
                        previousFrictionLambda = cached.frictionLambda;

                    }

                }
            }

            ContactConstraint constraint = new ContactConstraint(
                collision.bodyA,
                collision.bodyB,
                collision.worldPoint,
                collision.normal,
                collision.penetration,
                previousNormalLambda,
                previousFrictionLambda
            );

            contactConstraints.add(constraint);
            contactKeys.add(key);
        }

        // Reaplica os impulsos recuperados uma única vez.
        for (ContactConstraint constraint : contactConstraints) {
            constraint.update();
        }

        // Resolve repetidamente as restrições para melhorar a estabilidade.
        for (int i = 0; i < SOLVER_ITERATIONS; i++) {
            for (ContactConstraint constraint : contactConstraints) {
                constraint.solve(dt, false);
            }
        }

        // Salva os novos impulsos para o próximo quadro.
        for (int i = 0; i < contactConstraints.size(); i++) {

            ContactConstraint constraint = contactConstraints.get(i);
            ContactKey key = contactKeys.get(i);

            nextCache.put(
                key,
                new CachedContact(
                    constraint.normal,
                    constraint.getAccumulatedNormalLambda(),
                    constraint.getAccumulatedFrictionLambda()
                )
            );
        }

        // Contatos que desapareceram deixam de ser armazenados.
        contactCache = nextCache;

        // Imprime os impulsos finais somente durante os testes.
        if (debugContactConstraints) {
            for (ContactConstraint constraint : contactConstraints) {
                System.out.printf(
                    "Atrito=%.4f | Impulso normal=%.6f"
                        + " | Impulso tangencial=%.6f%n",
                    constraint.friction,
                    constraint.getAccumulatedNormalLambda(),
                    constraint.getAccumulatedFrictionLambda()
                );
            }
        }
    }


    /**
     * Corrige diretamente a posição dos corpos sobrepostos.
     */
    private void correctPositions() {

        // Repete a correção para reduzir progressivamente a penetração.
        for (int i = 0; i < POSITION_ITERATIONS; i++) {

            // Recalcula as colisões com as posições atuais.
            detectCollisions();

            // Não há nada para corrigir se não existirem colisões.
            if (collisions.isEmpty()) {
                return;
            }

            for (CollisionData collision : collisions) {

                PhysicsObject bodyA = collision.bodyA;
                PhysicsObject bodyB = collision.bodyB;

                // Corpos estáticos não podem ter suas posições alteradas.
                double invMassA = inverseMass(bodyA);
                double invMassB = inverseMass(bodyB);

                double totalInverseMass = invMassA + invMassB;

                // Se ambos não podem se mover, não há correção possível.
                if (totalInverseMass <= 0) {
                    continue;
                }

                // Desconsidera pequenas penetrações.
                double penetration =
                    Math.max(collision.penetration - POSITION_SLOP, 0);

                if (penetration <= 0) {
                    continue;
                }

                // Calcula a correção proporcional à penetração.
                Vector2 correction = collision.normal.scale(
                    POSITION_CORRECTION
                        * penetration / totalInverseMass
                );

                // Afasta os corpos proporcionalmente às suas massas inversas.
                bodyA.position = bodyA.position.subtract(
                    correction.scale(invMassA)
                );

                bodyB.position = bodyB.position.add(
                    correction.scale(invMassB)
                );
            }
        }
    }

    /**
     * Retorna a massa inversa do corpo.
     * Corpos estáticos possuem massa inversa igual a zero.
     */
    private static double inverseMass(PhysicsObject body) {
        return body.isStatic ? 0 : 1.0 / body.mass;
    }


    /**
     * Identifica um contato pelo par de objetos.
     * A comparação usa a identidade dos objetos, não suas posições.
     */
    private static class ContactKey {

        private final PhysicsObject bodyA;
        private final PhysicsObject bodyB;

        private ContactKey(PhysicsObject bodyA, PhysicsObject bodyB) {
            this.bodyA = bodyA;
            this.bodyB = bodyB;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof ContactKey)) {
                return false;
            }

            ContactKey other = (ContactKey) obj;

            return bodyA == other.bodyA && bodyB == other.bodyB;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(bodyA)
                + System.identityHashCode(bodyB);
        }
    }

    /**
     * Armazena os impulsos acumulados e a normal do contato anterior.
     */
    private static class CachedContact {

        private final Vector2 normal;
        private final double normalLambda;
        private final double frictionLambda;

        private CachedContact(
                Vector2 normal,
                double normalLambda,
                double frictionLambda) {

            this.normal = normal;
            this.normalLambda = normalLambda;
            this.frictionLambda = frictionLambda;
        }
    }

}
