package io.github.erp.service.criteria;

/*-
 * Erp System - Mark X No 7 (Jehoiada Series) Server ver 1.7.8
 * Copyright © 2021 - 2024 Edwin Njeru and the ERP System Contributors (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
import io.github.erp.domain.enumeration.CardCategoryFlag;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.github.erp.domain.CardCategoryType} entity. This class is used
 * in {@link io.github.erp.web.rest.CardCategoryTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /card-category-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CardCategoryTypeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CardCategoryFlag
     */
    public static class CardCategoryFlagFilter extends Filter<CardCategoryFlag> {

        public CardCategoryFlagFilter() {}

        public CardCategoryFlagFilter(CardCategoryFlagFilter filter) {
            super(filter);
        }

        @Override
        public CardCategoryFlagFilter copy() {
            return new CardCategoryFlagFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CardCategoryFlagFilter cardCategoryFlag;

    private StringFilter cardCategoryDescription;

    private Boolean distinct;

    public CardCategoryTypeCriteria() {}

    public CardCategoryTypeCriteria(CardCategoryTypeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cardCategoryFlag = other.cardCategoryFlag == null ? null : other.cardCategoryFlag.copy();
        this.cardCategoryDescription = other.cardCategoryDescription == null ? null : other.cardCategoryDescription.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CardCategoryTypeCriteria copy() {
        return new CardCategoryTypeCriteria(this);
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

    public CardCategoryFlagFilter getCardCategoryFlag() {
        return cardCategoryFlag;
    }

    public CardCategoryFlagFilter cardCategoryFlag() {
        if (cardCategoryFlag == null) {
            cardCategoryFlag = new CardCategoryFlagFilter();
        }
        return cardCategoryFlag;
    }

    public void setCardCategoryFlag(CardCategoryFlagFilter cardCategoryFlag) {
        this.cardCategoryFlag = cardCategoryFlag;
    }

    public StringFilter getCardCategoryDescription() {
        return cardCategoryDescription;
    }

    public StringFilter cardCategoryDescription() {
        if (cardCategoryDescription == null) {
            cardCategoryDescription = new StringFilter();
        }
        return cardCategoryDescription;
    }

    public void setCardCategoryDescription(StringFilter cardCategoryDescription) {
        this.cardCategoryDescription = cardCategoryDescription;
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
        final CardCategoryTypeCriteria that = (CardCategoryTypeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cardCategoryFlag, that.cardCategoryFlag) &&
            Objects.equals(cardCategoryDescription, that.cardCategoryDescription) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardCategoryFlag, cardCategoryDescription, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CardCategoryTypeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (cardCategoryFlag != null ? "cardCategoryFlag=" + cardCategoryFlag + ", " : "") +
            (cardCategoryDescription != null ? "cardCategoryDescription=" + cardCategoryDescription + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
