package fr.isen.twinmx.database;

import java.util.List;
import java.util.concurrent.Callable;

import fr.isen.twinmx.database.exceptions.MotoRepositoryException;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.listeners.MotoListener;
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

    public void deleteByNameAsync(final String name) throws RepositoryException {
        runAsync(this.realm, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return deleteByName(name);
            }
        });
    }

}
