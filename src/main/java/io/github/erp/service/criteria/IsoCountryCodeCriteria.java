package io.github.erp.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import io.github.erp.erp.resources.IsoCountryCodeResource;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.github.erp.domain.IsoCountryCode} entity. This class is used
 * in {@link IsoCountryCodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /iso-country-codes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class IsoCountryCodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter countryCode;

    private StringFilter countryDescription;

    private LongFilter placeholderId;

    private Boolean distinct;

    public IsoCountryCodeCriteria() {}

    public IsoCountryCodeCriteria(IsoCountryCodeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.countryDescription = other.countryDescription == null ? null : other.countryDescription.copy();
        this.placeholderId = other.placeholderId == null ? null : other.placeholderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public IsoCountryCodeCriteria copy() {
        return new IsoCountryCodeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            countryCode = new StringFilter();
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getCountryDescription() {
        return countryDescription;
    }

    public StringFilter countryDescription() {
        if (countryDescription == null) {
            countryDescription = new StringFilter();
        }
        return countryDescription;
    }

    public void setCountryDescription(StringFilter countryDescription) {
        this.countryDescription = countryDescription;
    }

    public LongFilter getPlaceholderId() {
        return placeholderId;
    }

    public LongFilter placeholderId() {
        if (placeholderId == null) {
            placeholderId = new LongFilter();
        }
        return placeholderId;
    }

    public void setPlaceholderId(LongFilter placeholderId) {
        this.placeholderId = placeholderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IsoCountryCodeCriteria that = (IsoCountryCodeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(countryDescription, that.countryDescription) &&
            Objects.equals(placeholderId, that.placeholderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryCode, countryDescription, placeholderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IsoCountryCodeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (countryDescription != null ? "countryDescription=" + countryDescription + ", " : "") +
            (placeholderId != null ? "placeholderId=" + placeholderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
