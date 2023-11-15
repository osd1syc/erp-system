package io.github.erp.domain;

/*-
 * Erp System - Mark VII No 5 (Gideon Series) Server ver 1.5.9
 * Copyright © 2021 - 2023 Edwin Njeru (mailnjeru@gmail.com)
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
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A CounterPartyDealType.
 */
@Entity
@Table(name = "counter_party_deal_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "counterpartydealtype")
public class CounterPartyDealType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "counterparty_deal_code", nullable = false, unique = true)
    private String counterpartyDealCode;

    @NotNull
    @Column(name = "counterparty_deal_type_details", nullable = false, unique = true)
    private String counterpartyDealTypeDetails;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "counterparty_deal_type_description")
    private String counterpartyDealTypeDescription;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CounterPartyDealType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCounterpartyDealCode() {
        return this.counterpartyDealCode;
    }

    public CounterPartyDealType counterpartyDealCode(String counterpartyDealCode) {
        this.setCounterpartyDealCode(counterpartyDealCode);
        return this;
    }

    public void setCounterpartyDealCode(String counterpartyDealCode) {
        this.counterpartyDealCode = counterpartyDealCode;
    }

    public String getCounterpartyDealTypeDetails() {
        return this.counterpartyDealTypeDetails;
    }

    public CounterPartyDealType counterpartyDealTypeDetails(String counterpartyDealTypeDetails) {
        this.setCounterpartyDealTypeDetails(counterpartyDealTypeDetails);
        return this;
    }

    public void setCounterpartyDealTypeDetails(String counterpartyDealTypeDetails) {
        this.counterpartyDealTypeDetails = counterpartyDealTypeDetails;
    }

    public String getCounterpartyDealTypeDescription() {
        return this.counterpartyDealTypeDescription;
    }

    public CounterPartyDealType counterpartyDealTypeDescription(String counterpartyDealTypeDescription) {
        this.setCounterpartyDealTypeDescription(counterpartyDealTypeDescription);
        return this;
    }

    public void setCounterpartyDealTypeDescription(String counterpartyDealTypeDescription) {
        this.counterpartyDealTypeDescription = counterpartyDealTypeDescription;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CounterPartyDealType)) {
            return false;
        }
        return id != null && id.equals(((CounterPartyDealType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CounterPartyDealType{" +
            "id=" + getId() +
            ", counterpartyDealCode='" + getCounterpartyDealCode() + "'" +
            ", counterpartyDealTypeDetails='" + getCounterpartyDealTypeDetails() + "'" +
            ", counterpartyDealTypeDescription='" + getCounterpartyDealTypeDescription() + "'" +
            "}";
    }
}
