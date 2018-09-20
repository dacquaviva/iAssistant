package gruppo_20.iassistant.model;

import java.io.Serializable;

public class Punto implements Serializable{
    private float x;
    private float y;
    private boolean terminatore;
    private boolean flusso;

    public Punto(float x, float y, boolean terminatore, boolean flusso) {
        this.x = x;
        this.y = y;
        this.terminatore = terminatore;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isTerminatore() {
        return terminatore;
    }

    public void setTerminatore(boolean terminatore) {
        this.terminatore = terminatore;
    }

    public boolean isFlusso() {
        return flusso;
    }

    public void setFlusso(boolean flusso) {
        this.flusso = flusso;
    }
}
