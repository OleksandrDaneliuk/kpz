import java.util.ArrayList;
import java.util.List;

public class UniversalRepository {

    public interface IReadOnlyRepository<T> {
        List<T> getAll();
        T getById(int id);
    }

    public interface ICrudRepository<T> {
        void add(T entity);
        void update(T entity);
        void delete(int id);
    }

    public interface IBulkImportable<T> {
        void bulkImport(List<T> entities);
    }

    public interface IExportable {
        void exportToCsv(String path);
    }

    public interface IPurgeable {
        void purgeAll();
    }

    public static class ReadOnlyAnalyticsRepository<T> implements IReadOnlyRepository<T>, IExportable {
        @Override
        public List<T> getAll() {
            return new ArrayList<>();
        }

        @Override
        public T getById(int id) {
            return null;
        }

        @Override
        public void exportToCsv(String path) {
            System.out.println("Exported analytics to " + path);
        }
    }

    public static class CacheRepository<T> implements IReadOnlyRepository<T>, ICrudRepository<T>, IPurgeable {
        @Override
        public List<T> getAll() {
            return new ArrayList<>();
        }

        @Override
        public T getById(int id) {
            return null;
        }

        @Override
        public void add(T entity) {
            System.out.println("Added to cache.");
        }

        @Override
        public void update(T entity) {
            System.out.println("Updated in cache.");
        }

        @Override
        public void delete(int id) {
            System.out.println("Removed from cache.");
        }

        @Override
        public void purgeAll() {
            System.out.println("Cache purged.");
        }
    }

    public static class AnalyticsReportService<T> {
        private final IReadOnlyRepository<T> repository;
        private final IExportable exporter;

        public AnalyticsReportService(IReadOnlyRepository<T> repository, IExportable exporter) {
            this.repository = repository;
            this.exporter = exporter;
        }

        public void generateReport(String path) {
            List<T> data = repository.getAll();
            exporter.exportToCsv(path);
        }
    }

    public static class CacheManagementService<T> {
        private final ICrudRepository<T> crudRepository;
        private final IPurgeable purgeable;

        public CacheManagementService(ICrudRepository<T> crudRepository, IPurgeable purgeable) {
            this.crudRepository = crudRepository;
            this.purgeable = purgeable;
        }

        public void refreshCache(T entity) {
            crudRepository.add(entity);
            purgeable.purgeAll();
        }
    }
}
