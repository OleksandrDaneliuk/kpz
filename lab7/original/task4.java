package variant2_universalrepository;

import java.util.ArrayList;
import java.util.List;

// Опис предметної області: Корпоративний шар доступу до даних та аналітики.
// Завдання коду: Обслуговування операцій зчитування, модифікації, імпорту, експорту та очищення баз даних.

public class UniversalRepository {

    public interface IUniversalDataRepository<T> {
        List<T> getAll();
        T getById(int id);
        void add(T entity);
        void update(T entity);
        void delete(int id);
        void bulkImport(List<T> entities);
        void exportToCsv(String path);
        void purgeAll();
    }

    public static class ReadOnlyAnalyticsRepository<T> implements IUniversalDataRepository<T> {
        @Override public List<T> getAll() { return new ArrayList<>(); }
        @Override public T getById(int id) { return null; }

        @Override public void add(T entity) { throw new UnsupportedOperationException("ReadOnly repository does not allow modifications."); }
        @Override public void update(T entity) { throw new UnsupportedOperationException("ReadOnly repository does not allow modifications."); }
        @Override public void delete(T entity) { throw new UnsupportedOperationException("ReadOnly repository does not allow modifications."); }
        @Override public void bulkImport(List<T> entities) { throw new UnsupportedOperationException("Bulk import is disabled for ReadOnly analytics."); }
        @Override public void exportToCsv(String path) { System.out.println("Exported analytics to " + path); }
        @Override public void purgeAll() { throw new UnsupportedOperationException("Purge operation is forbidden."); }
    }

    public static class CacheRepository<T> implements IUniversalDataRepository<T> {
        @Override public List<T> getAll() { return new ArrayList<>(); }
        @Override public T getById(int id) { return null; }
        @Override public void add(T entity) { System.out.println("Added to cache."); }
        @Override public void update(T entity) { System.out.println("Updated in cache."); }
        @Override public void delete(int id) { System.out.println("Removed from cache."); }
        @Override public void purgeAll() { System.out.println("Cache purged."); }

        @Override public void bulkImport(List<T> entities) { throw new UnsupportedOperationException("Bulk import is not supported in RAM cache."); }
        @Override public void exportToCsv(String path) { throw new UnsupportedOperationException("Cache repository cannot export files."); }
    }
}