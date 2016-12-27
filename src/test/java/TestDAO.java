import org.hibernate.annotations.NaturalId;
import pl.khuzzuk.dao.DAO;
import pl.khuzzuk.dao.Named;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class TestDAO {
    public static void main(String[] args) {
        DAO dao = new DAO();
        dao.initResolvers(EntityType.class);
        dao.saveEntity(new EntityType("name"));
    }

    @Entity
    public static class EntityType implements Named<String> {
        @Id
        @GeneratedValue
        private long id;
        @NaturalId
        private String name;

        public EntityType(String name) {
            this.name = name;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public void setId(long id) {
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
