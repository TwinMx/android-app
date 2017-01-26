package fr.isen.twinmx.TwinMaxApk;

/**
 * Created by Clement on 25/01/2017.
 */
public class TwinMaxApkMeasure {
    private int c0;
    private int c1;
    private int c2;
    private int c3;

    public TwinMaxApkMeasure(int c0, int c1, int c2, int c3) {
        this.c0 = c0;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }


    public TwinMaxApkMeasure(TwinMaxApkMeasure measure) {
        if(measure != null) {
            c0 = measure.get(0);
            c1 = measure.get(1);
            c2 = measure.get(2);
            c3 = measure.get(3);
        }
    }

    //Un autre constructeur pour faciliter
    //l'entrée des valeurs à la main
    //inutile dans le programme final
    public TwinMaxApkMeasure(int c){
        this.c0 = c ;
        this.c1 = c ;
        this.c2 = c ;
        this.c3 = c ;
    }

    //Un seul get pour les 4
    public int get(int i) {
        switch (i) {
            case 0:  return c0;
            case 1:  return c1;
            case 2:  return c2;
            case 3:  return c3;
            default: return 0;
        }
    }

    public void setC1(int c1) {
        this.c1 = c1;
    }

    public void setC2(int c2) {
        this.c2 = c2;
    }


    public void setC3(int c3) {
        this.c3 = c3;
    }

    public void setC0(int c0) {
        this.c0 = c0;
    }
}
