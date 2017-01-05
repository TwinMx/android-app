package fr.isen.twinmx.database;

import java.util.List;

import fr.isen.twinmx.database.exceptions.MotoRepositoryException;
import fr.isen.twinmx.database.listeners.MotoListener;
import fr.isen.twinmx.database.model.Moto;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by Clement on 05/01/2017.
 */

public class MotoRepository {


    private static MotoRepository instance = null;
    public static MotoRepository getInstance() {
        if (instance == null) {
            instance = new MotoRepository(RealmHelper.getRealm());
        }
        return instance;
    }

    private final Realm realm;

    private MotoRepository(Realm realm) {
        this.realm = realm;
    }

    private RealmQuery<Moto> getRepository() {
        return this.realm.where(Moto.class);
    }

    private void begin() {
        this.realm.beginTransaction();
    }

    private void end() {
        this.realm.commitTransaction();
    }

    public Moto create(Moto moto) throws MotoRepositoryException {
        try {
            begin();
            moto = this.realm.copyToRealm(moto);
            end();
            return moto;
        }
        catch(Exception ex) {
            throw new MotoRepositoryException("create", ex);
        }
    }


    public void deleteAll() throws MotoRepositoryException {
        try {
            begin();
            this.realm.deleteAll();
            end();
        }
        catch(Exception ex) {
            throw new MotoRepositoryException("deleteAll", ex);
        }
    }

    public void deleteAllAsync() {
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    deleteAll();
                } catch (MotoRepositoryException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void delete(Moto moto) throws MotoRepositoryException {
        try {
            begin();
            moto.deleteFromRealm();
            end();
        }
        catch(IllegalStateException ex) {
            throw new MotoRepositoryException("delete", ex);
        }
    }

    public boolean deleteByName(String name) throws MotoRepositoryException {
        try {
            begin();
            this.delete(this.getRepository().equalTo("name", name).findAll().first());
            end();
            return true;
        }
        catch(NullPointerException | ArrayIndexOutOfBoundsException ex) {
            throw new MotoRepositoryException("deleteByName", ex);
        }
    }

    public List<Moto> findAll() {
        begin();
        List<Moto> motos = this.getRepository().findAll();
        end();
        return motos;
    }

    public RealmResults<Moto> findAllAsync(RealmChangeListener<RealmResults<Moto>> changeListener) {
        RealmResults<Moto> results = this.getRepository().findAllAsync();
        if (changeListener != null) results.addChangeListener(changeListener);
        return results;
    }
    
}
