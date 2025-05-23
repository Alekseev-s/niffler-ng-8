package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.template.JdbcTransactionTemplate;
import guru.qa.niffler.data.template.XaTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepositoryHibernate = new SpendRepositoryHibernate();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.spendJdbcUrl());

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            return SpendJson.fromEntity(spendRepositoryHibernate.create(spendEntity));
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return xaTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(spendRepositoryHibernate.createCategory(categoryEntity));
        });
    }
}
