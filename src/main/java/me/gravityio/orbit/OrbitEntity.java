package me.gravityio.orbit;

public interface OrbitEntity {
    void setPos(float x, float y, float z);
    void setRot(float pitch, float yaw);

    float getX();
    float getY();
    float getZ();
}
