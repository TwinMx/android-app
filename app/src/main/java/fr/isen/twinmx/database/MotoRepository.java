package fr.isen.twinmx.database;

import com.github.mikephil.charting.data.Entry;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import fr.isen.twinmx.database.exceptions.MotoRepositoryException;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.database.model.Maintenance;
import fr.isen.twinmx.database.model.Moto;
import fr.isen.twinmx.database.model.Repository;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotoRepository extends Repository<Moto> {


    private static MotoRepository instance = null;

    public static MotoRepository getInstance() {
        if (instance == null) {
            instance = new MotoRepository();
        }
        return instance;
    }

    private MotoRepository() {
        super(Moto.class);
    }

    public Moto updateName(Moto moto, String newName) throws RepositoryException {
        Moto updatedMoto = new Moto(moto);
        updatedMoto.setName(newName);
        return update(updatedMoto);
    }

    public Moto updateImage(Moto moto, String newImage) throws RepositoryException {
        Moto updatedMoto = new Moto(moto);
        updatedMoto.setImage(newImage);
        return update(updatedMoto);
    }

    public Moto updateAddGraph(Moto moto, Date date, String note, List<List<Entry>> graphs) throws RepositoryException {
        begin();
        Long time = date.getTime();
        moto.addGraphs(time.toString(), note, graphs);
        moto = this.realm.copyToRealmOrUpdate(moto);
        end();
        return moto;
    }

    public boolean deleteByName(final String name) throws RepositoryException {
        try {
            begin();
            this.delete(this.getRepository().equalTo("name", name).findAll().first());
            end();
            return true;
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
            throw new MotoRepositoryException("deleteByName", ex);
        }
    }

    public void deleteMaintenance(Moto moto, final int maintenanceIndex) throws RepositoryException {
        begin();
        moto.removeMaintenance(maintenanceIndex);
        end();
    }

    public void deleteByNameAsync(final String name) throws RepositoryException {
        runAsync(this.realm, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return deleteByName(name);
            }
        });
    }
}
