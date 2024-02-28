package io.github.erp.domain;

/*-
 * Erp System - Mark X No 4 (Jehoiada Series) Server ver 1.7.4
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
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A DerivativeSubType.
 */
@Entity
@Table(name = "derivative_sub_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "derivativesubtype")
public class DerivativeSubType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "financial_derivative_sub_type_code", nullable = false, unique = true)
    private String financialDerivativeSubTypeCode;

    @NotNull
    @Column(name = "financial_derivative_sub_tye", nullable = false, unique = true)
    private String financialDerivativeSubTye;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "financial_derivative_subtype_details")
    private String financialDerivativeSubtypeDetails;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DerivativeSubType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFinancialDerivativeSubTypeCode() {
        return this.financialDerivativeSubTypeCode;
    }

    public DerivativeSubType financialDerivativeSubTypeCode(String financialDerivativeSubTypeCode) {
        this.setFinancialDerivativeSubTypeCode(financialDerivativeSubTypeCode);
        return this;
    }

    public void setFinancialDerivativeSubTypeCode(String financialDerivativeSubTypeCode) {
        this.financialDerivativeSubTypeCode = financialDerivativeSubTypeCode;
    }

    public String getFinancialDerivativeSubTye() {
        return this.financialDerivativeSubTye;
    }

    public DerivativeSubType financialDerivativeSubTye(String financialDerivativeSubTye) {
        this.setFinancialDerivativeSubTye(financialDerivativeSubTye);
        return this;
    }

    public void setFinancialDerivativeSubTye(String financialDerivativeSubTye) {
        this.financialDerivativeSubTye = financialDerivativeSubTye;
    }

    public String getFinancialDerivativeSubtypeDetails() {
        return this.financialDerivativeSubtypeDetails;
    }

    public DerivativeSubType financialDerivativeSubtypeDetails(String financialDerivativeSubtypeDetails) {
        this.setFinancialDerivativeSubtypeDetails(financialDerivativeSubtypeDetails);
        return this;
    }

    public void setFinancialDerivativeSubtypeDetails(String financialDerivativeSubtypeDetails) {
        this.financialDerivativeSubtypeDetails = financialDerivativeSubtypeDetails;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DerivativeSubType)) {
            return false;
        }
        return id != null && id.equals(((DerivativeSubType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DerivativeSubType{" +
            "id=" + getId() +
            ", financialDerivativeSubTypeCode='" + getFinancialDerivativeSubTypeCode() + "'" +
            ", financialDerivativeSubTye='" + getFinancialDerivativeSubTye() + "'" +
            ", financialDerivativeSubtypeDetails='" + getFinancialDerivativeSubtypeDetails() + "'" +
            "}";
    }
}
