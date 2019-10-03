package io.golos.commun4j.services.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.golos.commun4j.sharedmodel.CyberName;

public class SubscriptionsResponse {
    private List<CyberName> items;
    @Nullable
    private String sequenceKey;

    public SubscriptionsResponse(List<CyberName> items, @Nullable String sequenceKey) {
        this.items = items;
        this.sequenceKey = sequenceKey;
    }

    public List<CyberName> getItems() {
        return items;
    }

    public void setItems(List<CyberName> items) {
        this.items = items;
    }

    @Nullable
    public String getSequenceKey() {
        return sequenceKey;
    }

    public void setSequenceKey(@Nullable String sequenceKey) {
        this.sequenceKey = sequenceKey;
    }

    @Override
    public String toString() {
        return "SubscriptionsResponse{" +
                "items=" + items +
                ", sequenceKey='" + sequenceKey + '\'' +
                '}';
    }
}
