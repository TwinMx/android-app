package fr.isen.twinmx.database.model;

import java.util.List;
import java.util.concurrent.Callable;

import fr.isen.twinmx.database.RealmHelper;
import fr.isen.twinmx.database.exceptions.RepositoryException;
import fr.isen.twinmx.database.interfaces.AutoIncrement;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Clement on 05/01/2017.
 */

public abstract class Repository<T extends RealmObject> {

    private final Class<T> clazz;
    protected final Realm realm;

    public Repository(Class<T> clazz) {
        this.realm = RealmHelper.getRealm();
        this.clazz = clazz;
    }

    public RealmQuery<T> getRepository() {
        return this.realm.where(clazz);
    }

    public static <U> void runAsync(final Realm realm, final Callable<U> func) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void begin() {
        this.realm.beginTransaction();
    }

    public void end() {
        this.realm.commitTransaction();
    }

    public T create(final T t) throws RepositoryException {
        try {
            begin();
            if (t instanceof AutoIncrement) generateId((AutoIncrement) t);
            T item = this.realm.copyToRealmOrUpdate(t);
            end();
            return item;
        } catch (Exception ex) {
            throw new RepositoryException("create", ex);
        }
    }

    private void generateId(AutoIncrement t) {
        if (t.getId() == null) t.setId(this.getMaxId() + 1);
    }

    public void createAsync(final T t) throws RepositoryException {
        runAsync(this.realm, new Callable<T>() {
            @Override
            public T call() throws Exception {
                return create(t);
            }
        });
    }

    public void deleteAll() throws RepositoryException {
        try {
            begin();
            this.getRepository().findAll().deleteAllFromRealm();
            end();
        } catch (Exception ex) {
            throw new RepositoryException("deleteAll", ex);
        }
    }

    public void deleteAllAsync() {
        runAsync(this.realm, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteAll();
                return null;
            }
        });
    }

    public void delete(final T t) throws RepositoryException {
        try {
            begin();
            t.deleteFromRealm();
            end();
        } catch (Exception ex) {
            throw new RepositoryException("delete", ex);
        }
    }

    public void deleteAsync(final T t) throws RepositoryException {
        runAsync(this.realm, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                delete(t);
                return null;
            }
        });
    }

    public List<T> findAll() {
        begin();
        List<T> items = getRepository().findAll();
        end();
        return items;
    }

    public long getMaxId() {
        Number n = realm.where(clazz).max("id");
        return n != null ? n.longValue() : -1;
    }

    public RealmResults<T> findAllAsync(RealmChangeListener<RealmResults<T>> changeListener) {
        RealmResults<T> results = getRepository().findAllAsync();
        if (changeListener != null) results.addChangeListener(changeListener);
        return results;
    }

    public T findById(Long motoID) {
        begin();
        T result = getRepository().equalTo("id", motoID).findFirst();
        end();

        return result;
    }

}
