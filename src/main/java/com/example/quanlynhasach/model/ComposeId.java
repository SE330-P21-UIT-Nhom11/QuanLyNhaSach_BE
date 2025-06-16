package com.example.quanlynhasach.model;

import java.io.Serializable;
import java.util.Objects;

public class ComposeId implements Serializable {

    private int productId;
    private int authorId;

    public ComposeId() {
    }

    public ComposeId(int productId, int authorId) {
        this.productId = productId;
        this.authorId = authorId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ComposeId))
            return false;
        ComposeId that = (ComposeId) o;
        return productId == that.productId && authorId == that.authorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, authorId);
    }
}