package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Vector3D {

    public final double x;
    public final double y;
    public final double z;

    public Vector3D() {
        this.x = 0D;
        this.y = 0D;
        this.z = 0D;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3D getPlayersLookVec(EntityPlayer player) {
        return getVectorFromRotation(player.rotationPitch, player.rotationYawHead);
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
        return this.x == 0D && this.y == 0D && this.z == 0D;
    }

    /**
     * Returns the norm of the vector.
     */
    public double norm() {
        return MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double normSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double dotProduct(Vector3D otherVector) {
        return this.x * otherVector.x + this.y * otherVector.y + this.z * otherVector.z;
    }

    /**
     * Returns the absolute angle in between
     * the 2Dvector resulting of the projection of this onto the XZ plane and
     * the 2Dvector resulting of the projection of otherVector onto the XZ plane
     * Result is in degrees
     */
    public double getXZAngleDiffWithVector(Vector3D otherVector) {
        final double den = Math.sqrt((this.x * this.x + this.z * this.z) * (otherVector.x * otherVector.x + otherVector.z * otherVector.z));
        if (den < 1.0000000116860974E-7D) {
            return 0;
        }
        final double cos = (this.x * otherVector.x + this.z * otherVector.z) / den;
        if (cos > 1 || cos < -1) {
            return 0;
        }
        return Math.toDegrees(Math.acos(cos));
    }

    /**
     * Returns the 2D vector resulting of the projection of this onto the XZ plane
     */
    public Vector2D getProjectionInXZPlane() {
        return new Vector2D(this.x, this.z);
    }

    /**
     * Returns the length of the 2D vector resulting of the projection of this onto the XZ plane
     */
    public double normInXZPlane() {
        return this.getProjectionInXZPlane().norm();
    }

    /**
     * Returns the absolute angle in between this and otherVector
     * in the plane formed by those two vectors
     * Result is in degres
     */
    public double getAngleWithVector(Vector3D otherVector) {
        final double den = Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z) *
                (otherVector.x * otherVector.x + otherVector.y * otherVector.y + otherVector.z * otherVector.z));
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
        return new Vector3D(this.x * d, this.y * d, this.z * d);
    }

}
