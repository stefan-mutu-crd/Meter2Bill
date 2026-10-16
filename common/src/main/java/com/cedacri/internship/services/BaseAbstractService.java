package com.cedacri.internship.services;

import com.cedacri.internship.dtos.BaseSearchDto;
import com.cedacri.internship.repositories.specification.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Transactional(readOnly = true)
public abstract class BaseAbstractService<T, ID> {

    private final BaseRepository<T, ID> baseRepository;
    private final Class<T> entityClass;

    protected BaseAbstractService(BaseRepository<T, ID> baseRepository,
                                  Class<T> entityClass) {
        this.baseRepository = baseRepository;
        this.entityClass = entityClass;
    }

    public T findById(ID id) {
        return baseRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(
                entityClass.getSimpleName() + " not found with id: " + id));
    }

    public T searchOne(BaseSearchDto searchDto) {
        return baseRepository.searchOptional(searchDto.toCriteria())
                .orElseThrow(()
                        -> new EntityNotFoundException(entityClass.getSimpleName() + " not found"));
    }

    public List<T> search(BaseSearchDto searchDto) {
        return baseRepository.search(searchDto.toCriteria());
    }

    public Page<T> searchPage(BaseSearchDto searchDto) {
        return baseRepository.search(searchDto.toCriteria(), searchDto.toPageable());
    }

    public long count(BaseSearchDto searchDto) {
        return baseRepository.count(searchDto.toCriteria());
    }

    @Transactional
    public T create(T value) {
        return baseRepository.save(value);
    }

    @Transactional
    public T update(ID id, Consumer<T> changes) {
        T entity = findById(id);
        changes.accept(entity);
        return entity;
    }

    @Transactional
    public T update(ID id, T incoming) {
        return update(id, existing -> BeanUtils.copyProperties(incoming, existing));
    }

    @Transactional
    public void delete(ID id) {
        T entity = findById(id);
        baseRepository.delete(entity);
    }
}
