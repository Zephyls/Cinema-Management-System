package cinema.service;

import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

abstract class AbstractService {
    protected <T> List<T> findAll(Class<T> type) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("select e from " + type.getSimpleName() + " e", type).getResultList();
        }
    }

    protected <T> T findById(Class<T> type, Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(type, id);
        }
    }
}
