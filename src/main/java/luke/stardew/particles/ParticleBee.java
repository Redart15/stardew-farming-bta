package luke.stardew.particles;

import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.tessellator.TessellatorParticle;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.Global;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.Random;
import java.util.function.Consumer;

import static luke.stardew.StardewMod.MOD_ID;

public class ParticleBee extends Particle {
    public static final IconCoordinate bee1 = TextureRegistry.getTexture(MOD_ID + ":particle/bee");
    public static final IconCoordinate bee2 = TextureRegistry.getTexture(MOD_ID + ":particle/bee_2");
    public static final double MAX_DRIFT_RADIUS = 2.0D;
    private final float originalScale;

    static Random random = new Random();
    float speed = 1.2f;
    State state = State.INIT;

    // init
    Vector3d vec = new Vector3d();
    Vector3d start = new Vector3d();
    Vector3d next = new Vector3d();
    Vector3d dest = new Vector3d();


    // circling
    public static final int TIME_AT_FLOWER = Global.TICKS_PER_SECOND * 4;
    int timeAtFlowerSpend = 0;


    public ParticleBee(World world, double d, double d1, double d2, double d3, double d4, double d5) {
        super(world, d, d1, d2, d3, d4, d5);
        this.tex = TextureRegistry.getTexture(MOD_ID + ":particle/bee");
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;
        this.size *= 1.2f;
        this.originalScale = this.size;
        this.age = 1;
        this.speed *= (float) (random.nextGaussian() + 1.0F);
        this.lifetime = Global.TICKS_PER_SECOND * 20;
    }

    @Override
    public void render(@NotNull TessellatorParticle t, float partialTick) {
        if (this.state == State.DESPAWN) {
            this.size = Math.max(0, this.originalScale - this.originalScale * ((float) this.age / Global.TICKS_PER_SECOND * 10));
        }
        super.render(t, partialTick);
    }

    public void oldTick() {
        this.tex = this.age % 2 == 0 ? bee1 : bee2;

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.96;
        this.yd *= 0.96;
        this.zd *= 0.96;
        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }
    }

    public enum State {
        INIT(ParticleBee::initialize),
        PATHING(ParticleBee::pathing),
        CICRLING(ParticleBee::circling),
        RETURNING(ParticleBee::returning),
        DESPAWN(ParticleBee::empty);

        public final Consumer<ParticleBee> tick;

        State(Consumer<ParticleBee> tick) {
            this.tick = tick;
        }

        public void tick(ParticleBee bee) {
            tick.accept(bee);
        }
    }

    private static void empty(ParticleBee asThis) {
        /* bee is dead so need to do anything */
    }

    private static void initialize(ParticleBee asThis) {
        asThis.start.set(asThis.x, asThis.y, asThis.z);
        double x = asThis.x + random.nextGaussian() * 3;
        double z = asThis.z + random.nextGaussian() * 3;
        double y = asThis.y + random.nextGaussian();
        if (random.nextFloat() > 0.4) {
            double hy = asThis.world.getHeightValue((int) x, (int) z) + 1 + random.nextGaussian() * 0.2;
            if (Math.abs(hy - asThis.y) < 5) {
                y = hy;
            }
        }
        asThis.dest.set(x, y, z);
        asThis.state = State.PATHING;
    }

    private static void pathing(ParticleBee asThis) {
        if (asThis.moveBeeToDest(asThis.dest)) {
            asThis.state = State.CICRLING;
            asThis.next.set(asThis.dest);
            asThis.state.tick(asThis);
        }
    }

    private static void returning(ParticleBee asThis) {
        if (asThis.moveBeeToDest(asThis.start)) {
            asThis.state = State.DESPAWN;
        }
    }

    private static void circling(ParticleBee asThis) {
        if (asThis.timeAtFlowerSpend++ >= TIME_AT_FLOWER) {
            asThis.state = State.RETURNING;
            asThis.state.tick(asThis);
            return;
        }
        if (asThis.moveBeeToDest(asThis.next)) {
            double nx = asThis.dest.x() + random.nextGaussian() * 0.1;
            double ny = asThis.dest.y() + random.nextGaussian() * 0.1;
            double nz = asThis.dest.z() + random.nextGaussian() * 0.1;
            asThis.next.set(nx, ny, nz);
        }
    }


    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.changeTex();
        this.lerp();
        this.state.tick(this);
    }

    private void lerp() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    private void changeTex() {
        if (this.age % 3 == 0) {
            this.tex = this.age % 2 == 0 ? bee1 : bee2;
        }
    }

    protected void isOnGround() {
        if (this.onGround) {
            this.y += 0.1;
        }
    }

    private boolean moveBeeToDest(Vector3d dest) {
        double x = dest.x() - this.x;
        double y = dest.y() - this.y;
        double z = dest.z() - this.z;
        if (Math.abs(x) < 0.01 && Math.abs(y) < 0.01D && Math.abs(z) < 0.01D) {
            return true;
        }
        this.moveBee(x, y, z);
        return false;
    }

    private void moveBee(double x, double y, double z) {
        this.isOnGround();
        this.vec.set(x, y, z);
        float step = this.speed / Global.TICKS_PER_SECOND;
        if (this.vec.length() > step) {
            this.vec.normalize();
        }
        this.vec.mul(step);
        this.move(vec.x, vec.y, vec.z);
    }
}
