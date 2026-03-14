/*
package ru.ugaforever.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ugaforever.market.entity.Order;

import java.util.List;
import java.util.Optional;

*/
/**
 * JPQL запросы с загрузкой List<Item> в один запрос
 * против ленивой инициализации
 *//*


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.id DESC")
    List<Order> findAllWithItems();
}
*/
