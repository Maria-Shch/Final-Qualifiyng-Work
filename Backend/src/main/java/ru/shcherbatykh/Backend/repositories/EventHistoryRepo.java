package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.EventHistory;

import java.util.List;

@Repository
public interface EventHistoryRepo extends PagingAndSortingRepository<EventHistory, Long> {
    List<EventHistory> findAll(Specification<EventHistory> specification, Pageable pageable);
}
