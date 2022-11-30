package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Vector3D extends Vec3 {

    public Vector3D() {
        super(0D, 0D, 0D);
    }

    public Vector3D(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Creates a normalized Vector3D from pitch and yaw
     */
    public static Vector3D getVectorFromRotation(float pitch, float yaw) {
        final float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        final float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        final float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        final float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vector3D(f1 * f2, f3, f * f2);
    }

    public boolean isZero() {
        return this.xCoord == 0D && this.yCoord == 0D && this.zCoord == 0D;
    }

    /**
     * Returns the absolute angle in between
     * the 2Dvector resulting of the projection of this onto the XZ plane and
     * the 2Dvector resulting of the projection of otherVector onto the XZ plane
     * Result is in degrees
     */
    public double getXZAngleDiffWithVector(Vector3D otherVector) {
        final double den = Math.sqrt((this.xCoord * this.xCoord + this.zCoord * this.zCoord) * (otherVector.xCoord * otherVector.xCoord + otherVector.zCoord * otherVector.zCoord));
        if (den < 1.0000000116860974E-7D) {
            return 0;
        }
        final double cos = (this.xCoord * otherVector.xCoord + this.zCoord * otherVector.zCoord) / den;
        if (cos > 1 || cos < -1) {
            return 0;
        }
        return Math.toDegrees(Math.acos(cos));
    }

    /**
     * Returns the 2D vector resulting of the projection of this onto the XZ plane
     */
    public Vector2D getProjectionInXZPlane() {
        return new Vector2D(this.xCoord, this.zCoord);
    }

    /**
     * Returns the length of the 2D vector resulting of the projection of this onto the XZ plane
     */
    public double lengthVectorInXZPlane() {
        return this.getProjectionInXZPlane().lengthVector();
    }

    /**
     * Returns the absolute angle in between this and otherVector
     * in the plane formed by those two vectors
     * Result is in degres
     */
    public double getAngleWithVector(Vector3D otherVector) {
        final double den = Math.sqrt((this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord) *
                (otherVector.xCoord * otherVector.xCoord + otherVector.yCoord * otherVector.yCoord + otherVector.zCoord * otherVector.zCoord));
        if (den < 1.0000000116860974E-7D) {
            return 0D;
        }
        final double cos = this.dotProduct(otherVector) / den;
        if (cos > 1D || cos < -1D) {
            return 0D;
        }
        return Math.toDegrees(Math.acos(cos));
    }

    /**
     * Returns a new Vector3D that is the result of this * d
     */
    public Vector3D mulitply(double d) {
        return new Vector3D(this.xCoord * d, this.yCoord * d, this.zCoord * d);
    }

}
