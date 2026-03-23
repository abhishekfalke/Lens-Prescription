package com.example.lensprescription.utils;

/**
 * Stateless helpers for validating eye-prescription input fields.
 * All methods are static; instantiate only if DI framework requires it.
 */
public final class ValidationUtils {

    private ValidationUtils() {}

    // ─── Numeric range constants ───────────────────────────────────

    public static final float SPHERE_MIN    = -20.00f;
    public static final float SPHERE_MAX    =  20.00f;
    public static final float CYLINDER_MIN  = -10.00f;
    public static final float CYLINDER_MAX  =  10.00f;
    public static final int   AXIS_MIN      = 0;
    public static final int   AXIS_MAX      = 180;
    public static final float ADD_MIN       = 0.00f;
    public static final float ADD_MAX       = 4.00f;
    public static final float PD_MIN        = 20.00f;   // monocular
    public static final float PD_MAX        = 45.00f;   // monocular

    // ─── String guards ────────────────────────────────────────────

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        return !isBlank(name) && name.trim().length() >= 2;
    }

    // ─── Diopter parsing ─────────────────────────────────────────

    /**
     * Safely parse a diopter string (e.g. "-1.25", "+0.50", "0").
     * Returns {@code defaultValue} on failure.
     */
    public static float parseDiopter(String s, float defaultValue) {
        if (isBlank(s)) return defaultValue;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse an axis string (integer 0–180).
     * Returns {@code defaultValue} on failure.
     */
    public static int parseAxis(String s, int defaultValue) {
        if (isBlank(s)) return defaultValue;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // ─── Range validation ─────────────────────────────────────────

    public static boolean isValidSphere(float v) {
        return v >= SPHERE_MIN && v <= SPHERE_MAX;
    }

    public static boolean isValidCylinder(float v) {
        return v >= CYLINDER_MIN && v <= CYLINDER_MAX;
    }

    public static boolean isValidAxis(int v) {
        return v >= AXIS_MIN && v <= AXIS_MAX;
    }

    public static boolean isValidAdd(float v) {
        return v >= ADD_MIN && v <= ADD_MAX;
    }

    /**
     * Axis is only meaningful when there is cylinder correction.
     * If cylinder == 0, axis should also be 0 (or is irrelevant).
     */
    public static boolean isAxisRequiredAndValid(float cylinder, int axis) {
        if (cylinder == 0f) return true;          // no cylinder → axis doesn't matter
        return isValidAxis(axis) && axis != 0;    // axis 0 with CYL is unusual
    }

    public static boolean isValidMonocularPd(float v) {
        return v == 0f || (v >= PD_MIN && v <= PD_MAX);
    }

    // ─── Formatted error messages ─────────────────────────────────

    public static String sphereError(float v) {
        return isValidSphere(v) ? null
                : String.format("Sphere must be between %.2f and %.2f", SPHERE_MIN, SPHERE_MAX);
    }

    public static String cylinderError(float v) {
        return isValidCylinder(v) ? null
                : String.format("Cylinder must be between %.2f and %.2f", CYLINDER_MIN, CYLINDER_MAX);
    }

    public static String axisError(int v) {
        return isValidAxis(v) ? null
                : String.format("Axis must be between %d° and %d°", AXIS_MIN, AXIS_MAX);
    }

    public static String addError(float v) {
        return isValidAdd(v) ? null
                : String.format("Addition must be between %.2f and %.2f", ADD_MIN, ADD_MAX);
    }

    public static String pdError(float v) {
        return isValidMonocularPd(v) ? null
                : String.format("PD must be between %.1f mm and %.1f mm (or 0)", PD_MIN, PD_MAX);
    }

    // ─── Full-form validation result ─────────────────────────────

    public static class ValidationResult {
        public final boolean isValid;
        public final String  errorMessage;

        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid      = isValid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult ok()        { return new ValidationResult(true, null); }
        public static ValidationResult fail(String msg) { return new ValidationResult(false, msg); }
    }

    /**
     * Validates all fields of a prescription form at once.
     * Returns the first error encountered, or {@code ValidationResult.ok()}.
     */
    public static ValidationResult validatePrescription(
            String patientName,
            float rightSph, float rightCyl, int rightAxis, float rightAdd,
            float leftSph,  float leftCyl,  int leftAxis,  float leftAdd,
            float pdRight, float pdLeft) {

        if (!isValidName(patientName))
            return ValidationResult.fail("Patient name must be at least 2 characters.");

        // Right eye
        if (!isValidSphere(rightSph))    return ValidationResult.fail("Right eye: " + sphereError(rightSph));
        if (!isValidCylinder(rightCyl))  return ValidationResult.fail("Right eye: " + cylinderError(rightCyl));
        if (!isValidAxis(rightAxis))     return ValidationResult.fail("Right eye: " + axisError(rightAxis));
        if (!isValidAdd(rightAdd))       return ValidationResult.fail("Right eye: " + addError(rightAdd));

        // Left eye
        if (!isValidSphere(leftSph))     return ValidationResult.fail("Left eye: " + sphereError(leftSph));
        if (!isValidCylinder(leftCyl))   return ValidationResult.fail("Left eye: " + cylinderError(leftCyl));
        if (!isValidAxis(leftAxis))      return ValidationResult.fail("Left eye: " + axisError(leftAxis));
        if (!isValidAdd(leftAdd))        return ValidationResult.fail("Left eye: " + addError(leftAdd));

        // PD
        if (!isValidMonocularPd(pdRight)) return ValidationResult.fail("Right PD: " + pdError(pdRight));
        if (!isValidMonocularPd(pdLeft))  return ValidationResult.fail("Left PD: "  + pdError(pdLeft));

        return ValidationResult.ok();
    }
}