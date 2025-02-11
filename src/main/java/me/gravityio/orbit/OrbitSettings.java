package me.gravityio.orbit;

import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class OrbitSettings {
    private static final float TWO_PI = (float) (Math.PI * 2);

    public EasingType easeType = EasingType.LINEAR;
    public float orbitDistance = 5;
    public long orbitTimeMs = 5000;
    public float orbitFov = 40;
    public float rotations = 1;
    public float startAngleRad = 0;
    public float heightOffset = 0;
    public Vector3f orbitPos = null;

    public void setOrbitPos(Vector3f pos) {
        this.orbitPos = pos;
    }

    public boolean canOrbit() {
        return this.orbitPos != null;
    }

    public void setStartAngle(Vector3f start) {
        double dirx = start.x - this.orbitPos.x;
        double dirz = start.z - this.orbitPos.z;
        this.startAngleRad = (float) Math.atan2(dirx, dirz);
    }

    public void setStartDistance(Vector3f start) {
        Vector3f dir = new Vector3f();
        Vector3f.sub(this.orbitPos, start, dir);
        dir.y = 0;
        this.orbitDistance = dir.length();
    }

    public void setHeightOffset(Vector3f start) {
        Vector3f dir = new Vector3f();
        Vector3f.sub(start, this.orbitPos, dir);
        this.heightOffset = dir.y;
    }

    public void orbitStep(long endTime, OrbitEntity cameraEntity) {
        float p = this.getPercent(endTime);
        p = this.applyEasing(p) * this.rotations * TWO_PI;
        float rot = p + this.startAngleRad;
        double x = this.orbitPos.x + MathHelper.sin(rot) * this.orbitDistance;
        double z = this.orbitPos.z + MathHelper.cos(rot) * this.orbitDistance;
        cameraEntity.setPos((float) x, this.orbitPos.y + this.heightOffset, (float) z);
        this.lookAt(cameraEntity);
    }

    public void lookAt(OrbitEntity cameraEntity) {
        double diffx = this.orbitPos.x - cameraEntity.getX();
        double diffy = this.orbitPos.y - cameraEntity.getY();
        double diffz = this.orbitPos.z - cameraEntity.getZ();
        double hyp = MathHelper.sqrt_double(diffx * diffx + diffz * diffz);

        cameraEntity.setRot(
            (float) -(Math.atan2(diffy, hyp) * 180.0D / Math.PI),
            (float) ((Math.atan2(diffz, diffx) * 180.0D / Math.PI) - 90.0F)
        );
    }

    public float applyEasing(float p) {
        switch (this.easeType) {
            case EASE_IN:
                return (float) Easings.easeIn(p);
            case EASE_OUT:
                return (float) Easings.easeOut(p);
            case EASE_IN_OUT:
                return (float) Easings.easeInOutCubic(p);
        }
        return p;
    }

    public float getPercent(long endTime) {
        return (float) (endTime - System.currentTimeMillis()) / this.orbitTimeMs;
    }
}
