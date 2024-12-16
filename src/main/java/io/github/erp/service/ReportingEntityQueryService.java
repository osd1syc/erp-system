package io.github.erp.service;

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

import io.github.erp.domain.*; // for static metamodels
import io.github.erp.domain.ReportingEntity;
import io.github.erp.repository.ReportingEntityRepository;
import io.github.erp.repository.search.ReportingEntitySearchRepository;
import io.github.erp.service.criteria.ReportingEntityCriteria;
import io.github.erp.service.dto.ReportingEntityDTO;
import io.github.erp.service.mapper.ReportingEntityMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ReportingEntity} entities in the database.
 * The main input is a {@link ReportingEntityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReportingEntityDTO} or a {@link Page} of {@link ReportingEntityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReportingEntityQueryService extends QueryService<ReportingEntity> {

    private final Logger log = LoggerFactory.getLogger(ReportingEntityQueryService.class);

    private final ReportingEntityRepository reportingEntityRepository;

    private final ReportingEntityMapper reportingEntityMapper;

    private final ReportingEntitySearchRepository reportingEntitySearchRepository;

    public ReportingEntityQueryService(
        ReportingEntityRepository reportingEntityRepository,
        ReportingEntityMapper reportingEntityMapper,
        ReportingEntitySearchRepository reportingEntitySearchRepository
    ) {
        this.reportingEntityRepository = reportingEntityRepository;
        this.reportingEntityMapper = reportingEntityMapper;
        this.reportingEntitySearchRepository = reportingEntitySearchRepository;
    }

    /**
     * Return a {@link List} of {@link ReportingEntityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReportingEntityDTO> findByCriteria(ReportingEntityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ReportingEntity> specification = createSpecification(criteria);
        return reportingEntityMapper.toDto(reportingEntityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReportingEntityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReportingEntityDTO> findByCriteria(ReportingEntityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReportingEntity> specification = createSpecification(criteria);
        return reportingEntityRepository.findAll(specification, page).map(reportingEntityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReportingEntityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ReportingEntity> specification = createSpecification(criteria);
        return reportingEntityRepository.count(specification);
    }

    /**
     * Function to convert {@link ReportingEntityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReportingEntity> createSpecification(ReportingEntityCriteria criteria) {
        Specification<ReportingEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReportingEntity_.id));
            }
            if (criteria.getEntityName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEntityName(), ReportingEntity_.entityName));
            }
            if (criteria.getReportingCurrencyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getReportingCurrencyId(),
                            root -> root.join(ReportingEntity_.reportingCurrency, JoinType.LEFT).get(SettlementCurrency_.id)
                        )
                    );
            }
            if (criteria.getRetainedEarningsAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRetainedEarningsAccountId(),
                            root -> root.join(ReportingEntity_.retainedEarningsAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
