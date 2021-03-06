package com.eprovement.poptavka.dao.user;

import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.common.ResultCriteria;
import com.eprovement.poptavka.domain.demand.Category;
import com.eprovement.poptavka.domain.user.Supplier;
import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Juraj Martinka
 *         Date: 12.2.11
 */
public interface SupplierDao extends BusinessUserRoleDao<Supplier> {

    /**
     * Load all suppliers associated to the given locality (-ies) while applying additional criteria
     * <code>resultCriteria</code> if they are specified.
     *
     * @see com.eprovement.poptavka.dao.demand.DemandDao#getDemands(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)
     *
     * @param localities
     * @param resultCriteria additional restirction that will be set to result, can be null
     * @return collection of suppliers that are related to the given localities and adher to <code>resultCriteria</code>
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     */
    Set<Supplier> getSuppliers(Locality[] localities, ResultCriteria resultCriteria);

    /**
     * Optmized method for loading suppliers count for all localities in one query!
     *
     * @return list of maps, each map containing only 2 items
     *         ("locality" => locality, "suppliersCount" => suppliersCount)
     */
    List<Map<String, Object>> getSuppliersCountForAllLocalities();

    /**
     * Evaluate the number of suppliers associated to the given <code>locality</code>(-ies).
     * <p>
     * Use this method instead of {@link #getSuppliers(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)} if you want
     * to retrieve only number of suppliers - this method is far more lightweight than usage of
     * {@link #getSuppliers(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)}
     *      .size().
     *
     * @param localities
     * @return number of suppliers related to the <code>locality</code>(-ies).
     */
    long getSuppliersCount(Locality... localities);

    /**
     * Evaluates the number of suppliers associated to the given
     * <code>locality</code>(-ies) and <code>category</code>(-ies).
     * <p>
     * Use this method instead of {@link #getSuppliers(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)} if you want
     * to retrieve only number of suppliers - this method is far more lightweight than usage of
     * {@link #getSuppliers(com.eprovement.poptavka.domain.address.Locality[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)}
     *      .size().
     *
     * @param localities
     * @return number of suppliers related to the <code>locality</code>(-ies).
     */
    long getSuppliersCount(List<Category> categories, List<Locality> localities,
        ResultCriteria resultCriteria);

    long getSuppliersCountQuick(Locality locality);

    /**
     * Get count of all suppliers that belongs directly to the specified locality. No suppliers belonging to
     * any sublocality are included!
     */
    long getSuppliersCountWithoutChildren(Locality locality);

    /**
     * Load all suppliers associated to the given category (-ies) while applying additional criteria
     * <code>resultCriteria</code> if they are specified.
     *
     * @param categories
     * @param resultCriteria
     * @return collection of suppliers that are related to the given localities and adher to <code>resultCriteria</code>
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     */
    Set<Supplier> getSuppliers(Category[] categories, ResultCriteria resultCriteria);

    /**
     * Load all suppliers associated to the given category (-ies) and
     * category (-ies) - each must be associated to both - while applying
     * additional criteria <code>resultCriteria</code> if they are specified.
     *
     * <p>
     * This method finds suppliers which belong directly to any category from given {@code categories}
     * and any locality from given {@code localities}
     * as well as suppliers which belong to any child category of given {@code categories}
     * and any child locality of given {@code localities}
     * </p>
     *
     * @param categories
     * @param localities
     * @param resultCriteria
     * @return collection of suppliers that are related to the given localities and adher to <code>resultCriteria</code>
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     */
    Set<Supplier> getSuppliers(List<Category> categories, List<Locality> localities, ResultCriteria resultCriteria);

    /**
     * Load all suppliers associated to any category (or its parent or its children) from given categories
     *      AND to any locality (or its parent or its children) from given localities.
     * This is somewhat opposite to the functionality provided by {@link #getSuppliers(java.util.List, java.util.List,
     * com.eprovement.poptavka.domain.common.ResultCriteria)}.
     *
     * <p>
     *     This is especially suitable for searching all potential suppliers which belong to given {@code categories}
     *     and {@code localities}.
     * </p>
     * Example: <br />
     * <ul>
     *  <li>Supplier {@code S1} registered himself for locality Alabama and category Electronics.</li
     *  <li>Client {@code C1} created demand with locality Montgomery (capital city of Alabama)
     *  and category 'Desktop computers' (child category of Electronics).</li>
     * </ul>
     * As a result this method returns collection containing supplier {@code S1}
     * when passing locality 'Montgomery' and category 'Desktop computers' as arguments to this method.
     *
     * @param categories list of categories, supplier must be associated either directly to any of these categories
     *                   or to some parent or to some children
     * @param localities list of localities, supplier must be associated either directly to any of these localities
     *                   or to some parent or to some children
     * @param resultCriteria filtering criteria
     * @return collection of suppliers that are related to the given localities and adher to <code>resultCriteria</code>
     * @throws IllegalStateException if <code>resultCriteria</code> specifies order by columns
     */
    Set<Supplier> getSuppliersIncludingParentsAndChildren(List<Category> categories, List<Locality> localities,
        ResultCriteria resultCriteria);

    /**
     * Optmized method for loading suppliers count for all categories in one query!
     *
     * @return list of maps, each map containing only 2 items
     *              ("category" => category, "suppliersCount" => suppliersCount)
     */
    List<Map<String, Object>> getSuppliersCountForAllCategories();

    /**
     * Evaluate the number of suppliers associated to the given <code>category</code>(-ies).
     * <p>
     * Use this method instead of {@link #getSuppliers(com.eprovement.poptavka.domain.demand.Category[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)} if you want
     * to retrieve only number of suppliers - this method is far more lightweight than usage of
     * {@link #getSuppliers(com.eprovement.poptavka.domain.demand.Category[],
     * com.eprovement.poptavka.domain.common.ResultCriteria)}
     *      .size().
     * </p>
     *
     * @param categories
     * @return number of suppliers related to the <code>category</code>(-ies).
     */
    long getSuppliersCount(Category... categories);

    long getSuppliersCountQuick(Category category);

    /**
     * Get count of all suppliers that belongs directly to the specified category. No suppliers belonging to
     * any subcategory are included!
     */
    long getSuppliersCountWithoutChildren(Category category);

    /**
     * Increments supplier counts for given categories.
     * @param categoryIds list of category ids to be updated
     */
    void incrementCategorySupplierCount(Collection<Long> categoryIds);

    /**
     * Decrements supplier counts for given categories.
     * @param categoryIds list of category ids to be updated
     */
    void decrementCategorySupplierCount(Collection<Long> categoryIds);

    /**
     * Increments supplier counts for given localities.
     * @param localityIds list of category ids to be updated
     */
    void incrementLocalitySupplierCount(Collection<Long> localityIds);

    /**
     * Decrements supplier counts for given localities.
     * @param localityIds list of category ids to be updated
     */
    void decrementLocalitySupplierCount(Collection<Long> localityIds);
}
