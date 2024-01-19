package fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils;

import net.minecraft.util.MathHelper;

public class Vector2D {

    public final double u;
    public final double v;

    public Vector2D() {
        this.u = 0D;
        this.v = 0D;
    }

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

    public boolean isZero() {
        return this.u == 0D && this.v == 0D;
    }

    /**
     * Returns the norm of the vector.
     */
    public double norm() {
        return Math.sqrt(this.u * this.u + this.v * this.v);
    }

    /**
     * In degres
     */
    public double getAngleWithOrigin() {
        final double lengthVector = this.norm();
        if (lengthVector < 1.0000000116860974E-7D) {
            return 0D;
        }
        final double cos = this.u / lengthVector;
        if (cos > 1) {
            return 0D;
        } else if (cos < -1) {
            return 180D;
        }
        return Math.toDegrees(Math.acos(cos));
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
        if (cos > 1) {
            return 0D;
        } else if (cos < -1) {
            return 180D;
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

    @Override
    public String toString() {
        return "{" + String.format("%.4f", this.u) +
                ", " + String.format("%.4f", this.v) +
                '}';
    }

}
