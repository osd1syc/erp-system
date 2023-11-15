package io.github.erp.service.dto;

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
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.erp.domain.CrbReportRequestReasons} entity.
 */
public class CrbReportRequestReasonsDTO implements Serializable {

    private Long id;

    @NotNull
    private String creditReportRequestReasonTypeCode;

    @NotNull
    private String creditReportRequestReasonType;

    @Lob
    private String creditReportRequestDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreditReportRequestReasonTypeCode() {
        return creditReportRequestReasonTypeCode;
    }

    public void setCreditReportRequestReasonTypeCode(String creditReportRequestReasonTypeCode) {
        this.creditReportRequestReasonTypeCode = creditReportRequestReasonTypeCode;
    }

    public String getCreditReportRequestReasonType() {
        return creditReportRequestReasonType;
    }

    public void setCreditReportRequestReasonType(String creditReportRequestReasonType) {
        this.creditReportRequestReasonType = creditReportRequestReasonType;
    }

    public String getCreditReportRequestDetails() {
        return creditReportRequestDetails;
    }

    public void setCreditReportRequestDetails(String creditReportRequestDetails) {
        this.creditReportRequestDetails = creditReportRequestDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrbReportRequestReasonsDTO)) {
            return false;
        }

        CrbReportRequestReasonsDTO crbReportRequestReasonsDTO = (CrbReportRequestReasonsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, crbReportRequestReasonsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CrbReportRequestReasonsDTO{" +
            "id=" + getId() +
            ", creditReportRequestReasonTypeCode='" + getCreditReportRequestReasonTypeCode() + "'" +
            ", creditReportRequestReasonType='" + getCreditReportRequestReasonType() + "'" +
            ", creditReportRequestDetails='" + getCreditReportRequestDetails() + "'" +
            "}";
    }
}
