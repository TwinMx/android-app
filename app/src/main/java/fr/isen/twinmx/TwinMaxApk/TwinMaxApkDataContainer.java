package fr.isen.twinmx.TwinMaxApk;

import java.util.ArrayList;
import java.util.List;

import fr.isen.twinmx.fragments.TwinMaxApkChartComponent;

/**
 * Created by Clement on 25/01/2017.
 */

public class TwinMaxApkDataContainer {
    private final TwinMaxApkChartComponent chartComponent;
    private List<TwinMaxApkMeasure> dataContainer;
    private int graphToSkip;
    private int sizeOfGraph;


    public int getSizeOfList() {
        synchronized (dataContainer) {
            return dataContainer.size();
        }
    }

    public void setSizeOfGraph(int sizeOfGraph) {
        this.sizeOfGraph = sizeOfGraph;
    }

    public int getSizeOfGraph() {
        return sizeOfGraph;
    }

    public TwinMaxApkDataContainer(TwinMaxApkChartComponent chartComponent) {
        dataContainer = new ArrayList<>();
        this.chartComponent = chartComponent;
        graphToSkip = 0;
        sizeOfGraph = 200;
    }

    public boolean isEmpty() {
        synchronized (dataContainer) {
            return dataContainer.isEmpty();
        }
    }


    private boolean isFirst = true;
    private int[] precIndex = {0,0};
    public synchronized List<TwinMaxApkMeasure> getSub(int indexMin, int indexMax) {
        if(isFirst) {
            isFirst = false;
        } else {
            for(int i=precIndex[0] ; i<=precIndex[1]; i++) {
                dataContainer.remove(0);
            }
        }
        try {
            precIndex[0] = indexMin;
            precIndex[1] = indexMax;
            return dataContainer.subList(indexMin, indexMax);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private boolean isFFirst = true;

    public List<TwinMaxApkMeasure> getGraphValues() {
        synchronized (dataContainer) {
            List<TwinMaxApkMeasure> tempList = new ArrayList<>();
            for (int i = 0; i < sizeOfGraph; i++) {
                if (!dataContainer.isEmpty() && dataContainer.size() > 0) {
                    tempList.add(dataContainer.remove(0));
                }
            }


            if (mSkipsGraph == null || mSkipsGraph.getState().compareTo(Thread.State.TERMINATED) == 0) {
                mSkipsGraph = new SkipGraphs();
                mSkipsGraph.start();
            }

            return tempList;
            //return dataContainer.subList(0,200);
        }
    }
    private SkipGraphs mSkipsGraph;
    private class SkipGraphs extends Thread {
        @Override
        public void run() {
            synchronized(dataContainer) {
                for (int i = 0; i < graphToSkip; i++) {
                    getGraphValues();
                }
            }
        }
    }


    public synchronized TwinMaxApkMeasure getFirst() {
        if(isFFirst) {
            isFFirst = false;
        } else if(!dataContainer.isEmpty() && dataContainer.size() > 0){
            dataContainer.remove(0);
        }
        if(!dataContainer.isEmpty() && dataContainer.size() > 0) {
            return dataContainer.get(0);
        } else {
            return null;
        }
    }

    public int sizeOfList() {
        return dataContainer.size();
    }


    public int getGraphToSkip() {
        return graphToSkip;
    }

    public void addValue(TwinMaxApkMeasure newVal) {
        synchronized (dataContainer) {
            if (dataContainer != null) {

                dataContainer.add(newVal);
                graphToSkip = (int) (dataContainer.size() / 1000) + 2;
                //Log.e("Taille data clean", "Taille CLEAN : " + dataContainer.size() + "\n" +"TO SKIP : " + graphToSkip);
            }
        }
    }
}
