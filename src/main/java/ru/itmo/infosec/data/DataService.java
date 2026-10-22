package ru.itmo.infosec.data;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
public class DataService {
    private final DataItemRepository dataItems;

    public DataService(DataItemRepository dataItems) {
        this.dataItems = dataItems;
    }

    @Transactional(readOnly = true)
    public List<DataResponse> findAll(String username) {
        return dataItems.findAllByOwnerUsernameOrderByCreatedAtDesc(username).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public DataResponse create(String username, CreateDataRequest request) {
        DataItem saved = dataItems.save(new DataItem(username, request.title().trim(), request.content().trim()));
        return toResponse(saved);
    }

    private DataResponse toResponse(DataItem item) {
        return new DataResponse(
            item.getId(),
            HtmlUtils.htmlEscape(item.getTitle()),
            HtmlUtils.htmlEscape(item.getContent()),
            item.getCreatedAt()
        );
    }
}

