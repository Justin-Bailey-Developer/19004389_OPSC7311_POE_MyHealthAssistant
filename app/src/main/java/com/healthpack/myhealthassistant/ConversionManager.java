package com.healthpack.myhealthassistant;

public class ConversionManager {

    double inValue, outValue;

    public double KilogramsToPounds(double in){

        inValue = in;

        outValue = Math.round((inValue * 2.20462) * 100.0) / 100.0;

        return outValue;
    }

    public double PoundsToKilograms(double in){

        inValue = in;

        outValue = Math.round((inValue * 0.453592) * 100.0) / 100.0;

        return outValue;
    }

    public double MetresToInches(double in) {

        inValue = in;

        outValue = Math.round((inValue * 39.3701) * 100.0) / 100.0;

        return outValue;
    }

    public double InchesToMetres(double in) {

        inValue = in;

        outValue = Math.round((inValue * 0.0254) * 100.0) / 100.0;

        return outValue;
    }
}
