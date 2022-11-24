package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;

import net.minecraft.util.MathHelper;

public class Vector2D {

    public final double u;
    public final double v;

    public Vector2D(double u, double v) {
        if (u == -0.0D) {
            u = 0.0D;
        }
        if (v == -0.0D) {
            v = 0.0D;
        }
        this.u = u;
        this.v = v;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector() {
        return Math.sqrt(this.u * this.u + this.v * this.v);
    }

    /**
     * In degres
     */
    public double getAngleWithOrigin() {
        if (this.u == 0 && this.v == 0) {
            return 0;
        }
        final double lengthVector = this.lengthVector();
        if (lengthVector < 1.0000000116860974E-7D) {
            return 0;
        }
        return Math.toDegrees(Math.acos(this.u / lengthVector));
    }

    /**
     * Returns the scalar product
     */
    public double dotProduct(Vector2D otherVector) {
        return this.u * otherVector.u + this.v * otherVector.v;
    }

    /**
     * Returns the absolute angle in between this and otherVector in degrees
     */
    public double getAngleWithVector(Vector2D otherVector) {
        final double den = Math.sqrt((this.u * this.u + this.v * this.v) * (otherVector.u * otherVector.u + otherVector.v * otherVector.v));
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
     * Creates a Vector2D in the XZ plane from pitch and yaw
     */
    public static Vector2D getVectorFromRotation(float pitch, float yaw) {
        final float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        final float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        final float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        return new Vector2D(f1 * f2, f * f2);
    }

}
