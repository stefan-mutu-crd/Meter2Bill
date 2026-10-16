package com.cedacri.internship.services;

import com.cedacri.internship.dtos.BaseSearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Consumer;

public interface Operations<T, ID> {
    public T findById(ID id);

    public T searchOne(BaseSearchDto searchDto);

    public List<T> search(BaseSearchDto searchDto);

    public Page<T> searchPage(BaseSearchDto searchDto);

    public long count(BaseSearchDto searchDto);

    public T create(T value);

    public T update(ID id, Consumer<T> changes);

    public T update(ID id, T incoming);

    public void delete(ID id);
}
