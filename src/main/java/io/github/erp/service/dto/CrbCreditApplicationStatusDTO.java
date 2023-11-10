package io.github.erp.service.dto;

/*-
 * Erp System - Mark VII No 2 (Gideon Series) Server ver 1.5.6
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
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.erp.domain.CrbCreditApplicationStatus} entity.
 */
public class CrbCreditApplicationStatusDTO implements Serializable {

    private Long id;

    @NotNull
    private String crbCreditApplicationStatusTypeCode;

    @NotNull
    private String crbCreditApplicationStatusType;

    @Lob
    private String crbCreditApplicationStatusDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrbCreditApplicationStatusTypeCode() {
        return crbCreditApplicationStatusTypeCode;
    }

    public void setCrbCreditApplicationStatusTypeCode(String crbCreditApplicationStatusTypeCode) {
        this.crbCreditApplicationStatusTypeCode = crbCreditApplicationStatusTypeCode;
    }

    public String getCrbCreditApplicationStatusType() {
        return crbCreditApplicationStatusType;
    }

    public void setCrbCreditApplicationStatusType(String crbCreditApplicationStatusType) {
        this.crbCreditApplicationStatusType = crbCreditApplicationStatusType;
    }

    public String getCrbCreditApplicationStatusDetails() {
        return crbCreditApplicationStatusDetails;
    }

    public void setCrbCreditApplicationStatusDetails(String crbCreditApplicationStatusDetails) {
        this.crbCreditApplicationStatusDetails = crbCreditApplicationStatusDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrbCreditApplicationStatusDTO)) {
            return false;
        }

        CrbCreditApplicationStatusDTO crbCreditApplicationStatusDTO = (CrbCreditApplicationStatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, crbCreditApplicationStatusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CrbCreditApplicationStatusDTO{" +
            "id=" + getId() +
            ", crbCreditApplicationStatusTypeCode='" + getCrbCreditApplicationStatusTypeCode() + "'" +
            ", crbCreditApplicationStatusType='" + getCrbCreditApplicationStatusType() + "'" +
            ", crbCreditApplicationStatusDetails='" + getCrbCreditApplicationStatusDetails() + "'" +
            "}";
    }
}
