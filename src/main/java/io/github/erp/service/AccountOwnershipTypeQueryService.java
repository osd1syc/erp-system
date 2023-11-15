package io.github.erp.service;

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
import io.github.erp.domain.*; // for static metamodels
import io.github.erp.domain.AccountOwnershipType;
import io.github.erp.repository.AccountOwnershipTypeRepository;
import io.github.erp.repository.search.AccountOwnershipTypeSearchRepository;
import io.github.erp.service.criteria.AccountOwnershipTypeCriteria;
import io.github.erp.service.dto.AccountOwnershipTypeDTO;
import io.github.erp.service.mapper.AccountOwnershipTypeMapper;
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
 * Service for executing complex queries for {@link AccountOwnershipType} entities in the database.
 * The main input is a {@link AccountOwnershipTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AccountOwnershipTypeDTO} or a {@link Page} of {@link AccountOwnershipTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AccountOwnershipTypeQueryService extends QueryService<AccountOwnershipType> {

    private final Logger log = LoggerFactory.getLogger(AccountOwnershipTypeQueryService.class);

    private final AccountOwnershipTypeRepository accountOwnershipTypeRepository;

    private final AccountOwnershipTypeMapper accountOwnershipTypeMapper;

    private final AccountOwnershipTypeSearchRepository accountOwnershipTypeSearchRepository;

    public AccountOwnershipTypeQueryService(
        AccountOwnershipTypeRepository accountOwnershipTypeRepository,
        AccountOwnershipTypeMapper accountOwnershipTypeMapper,
        AccountOwnershipTypeSearchRepository accountOwnershipTypeSearchRepository
    ) {
        this.accountOwnershipTypeRepository = accountOwnershipTypeRepository;
        this.accountOwnershipTypeMapper = accountOwnershipTypeMapper;
        this.accountOwnershipTypeSearchRepository = accountOwnershipTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AccountOwnershipTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AccountOwnershipTypeDTO> findByCriteria(AccountOwnershipTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AccountOwnershipType> specification = createSpecification(criteria);
        return accountOwnershipTypeMapper.toDto(accountOwnershipTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AccountOwnershipTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AccountOwnershipTypeDTO> findByCriteria(AccountOwnershipTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AccountOwnershipType> specification = createSpecification(criteria);
        return accountOwnershipTypeRepository.findAll(specification, page).map(accountOwnershipTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AccountOwnershipTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AccountOwnershipType> specification = createSpecification(criteria);
        return accountOwnershipTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link AccountOwnershipTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AccountOwnershipType> createSpecification(AccountOwnershipTypeCriteria criteria) {
        Specification<AccountOwnershipType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AccountOwnershipType_.id));
            }
            if (criteria.getAccountOwnershipTypeCode() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getAccountOwnershipTypeCode(), AccountOwnershipType_.accountOwnershipTypeCode)
                    );
            }
            if (criteria.getAccountOwnershipType() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getAccountOwnershipType(), AccountOwnershipType_.accountOwnershipType)
                    );
            }
        }
        return specification;
    }
}
