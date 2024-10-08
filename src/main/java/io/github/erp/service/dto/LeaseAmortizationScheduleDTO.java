package io.github.erp.service.dto;

/*-
 * Erp System - Mark X No 10 (Jehoiada Series) Server ver 1.8.2
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
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.erp.domain.LeaseAmortizationSchedule} entity.
 */
public class LeaseAmortizationScheduleDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID identifier;

    private LeaseLiabilityDTO leaseLiability;

    private IFRS16LeaseContractDTO leaseContract;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public LeaseLiabilityDTO getLeaseLiability() {
        return leaseLiability;
    }

    public void setLeaseLiability(LeaseLiabilityDTO leaseLiability) {
        this.leaseLiability = leaseLiability;
    }

    public IFRS16LeaseContractDTO getLeaseContract() {
        return leaseContract;
    }

    public void setLeaseContract(IFRS16LeaseContractDTO leaseContract) {
        this.leaseContract = leaseContract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeaseAmortizationScheduleDTO)) {
            return false;
        }

        LeaseAmortizationScheduleDTO leaseAmortizationScheduleDTO = (LeaseAmortizationScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, leaseAmortizationScheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeaseAmortizationScheduleDTO{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", leaseLiability=" + getLeaseLiability() +
            ", leaseContract=" + getLeaseContract() +
            "}";
    }
}
