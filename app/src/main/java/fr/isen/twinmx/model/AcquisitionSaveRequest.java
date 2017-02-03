package fr.isen.twinmx.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.isen.twinmx.database.MotoRepository;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.fragments.LimitedEntryList;

/**
 * Created by Clement on 02/02/2017.
 */

public class AcquisitionSaveRequest {

    private Moto moto;
    private Date date;
    private String note;
    private List<List<Entry>> graphs;

    private static List<List<Entry>> toList(List<LimitedEntryList> graphs) {
        if (graphs != null && graphs.size() > 0) {
            List<List<Entry>> list = new ArrayList<>(graphs.size());
            for(LimitedEntryList entries : graphs) {
                if (entries != null) {
                    list.add(entries.toList());
                }
            }
            return list;
        }
        return null;
    }

    public AcquisitionSaveRequest(List<LimitedEntryList> graphs) {
        this(null, null, toList(graphs));
    }

    public AcquisitionSaveRequest(Moto moto, String note, List<List<Entry>> graphs) {
        this.date = new Date();
        this.moto = moto;
        this.note = note;
        this.graphs = graphs;
    }

    public void setMoto(Moto moto) {
        this.moto = moto;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setGraphs(List<List<Entry>> graphs) {
        this.graphs = graphs;
    }

    private boolean isSaveable() {
        boolean isOk = this.moto != null && this.note != null && this.graphs != null;
        isOk = this.graphs.size() > 0 && isOk;
        int size = -1;
        for(List<Entry> entries : this.graphs) {
            if (size == -1) { //First
                size = entries.size();
                isOk = size > 0 && isOk;
            } else {
                isOk = entries.size() == size && isOk;
            }
        }
        return isOk;
    }

    public boolean save() {
        if (graphs == null || graphs.isEmpty()) {
            graphs = new ArrayList<List<Entry>>(4) {{
               for(int i = 0; i < 4; i++) {
                   add(new ArrayList<Entry>(200) {{
                       for (int j = 0; j < 200; j++) {
                           add(new Entry(j, (int) Math.random() * 1000));
                       }
                   }});
               }
            }};
        }

        if (isSaveable()) {
            try {
                MotoRepository.getInstance().updateAddGraph(moto, date, note, graphs);
                return true;
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isNewMoto() {
        return moto == null;
    }

    public boolean createNewMoto(String motoName) {
        if (isNewMoto() && motoName != null && motoName.length() > 0) {
            try {
                MotoRepository.getInstance().create(new Moto(motoName));
                return true;
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
