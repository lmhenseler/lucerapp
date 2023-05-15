package com.luftce.lucerapp.models;

public class Edom {
    private String edom;
    private float potencia;

    public Edom(String edom, float potencia) {
        this.edom = edom;
        this.potencia = potencia;
    }

    public String getEdom() {
        return edom;
    }

    public void setEdom(String edom) {
        this.edom = edom;
    }

    public float getPotencia() {
        return potencia;
    }

    public void setPotencia(float potencia) {
        this.potencia = potencia;
    }

}
