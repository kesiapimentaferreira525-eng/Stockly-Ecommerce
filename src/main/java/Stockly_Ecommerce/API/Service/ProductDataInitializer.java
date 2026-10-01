package Stockly_Ecommerce.API.Service;

import Stockly_Ecommerce.API.Model.Category;
import Stockly_Ecommerce.API.Model.Product;
import Stockly_Ecommerce.API.Repository.CategoryRepository;
import Stockly_Ecommerce.API.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductDataInitializer implements CommandLineRunner {

    private static final List<ProductSeed> PRODUCTS = List.of(
            new ProductSeed("AUR-CAF-001", "Métodos", "Cafeteira italiana 6 xícaras",
                    "Cafeteira italiana de alumínio para preparar café encorpado.", "159.90", 12),
            new ProductSeed("AUR-UTL-001", "Acessórios", "Caneca cerâmica artesanal",
                    "Caneca de cerâmica feita para acompanhar seu café do dia a dia.", "54.90", 4),
            new ProductSeed("AUR-CAF-002", "Cafés Especiais", "Café especial em grãos 250 g",
                    "Café especial em grãos, torrado em pequenos lotes.", "39.90", 2),
            new ProductSeed("AUR-ACE-001", "Métodos", "Moedor manual de café",
                    "Moedor manual compacto com ajuste de moagem.", "119.90", 0),
            new ProductSeed("AUR-ACE-002", "Acessórios", "Balança digital para café",
                    "Balança digital de precisão para medir café e água.", "89.90", 8),
            new ProductSeed("MET-V60-02", "Métodos", "Suporte V60 Cerâmica 02",
                    "Suporte de cerâmica tamanho 02 para preparo de café coado.", "129.00", 0),
            new ProductSeed("MET-AERO-01", "Métodos", "Aeropress Clear",
                    "Cafeteira manual transparente para preparar café por pressão.", "319.00", 7),
            new ProductSeed("MET-V60-KIT", "Métodos", "Kit V60 com servidor de vidro",
                    "Conjunto com suporte V60, servidor de vidro e colher dosadora.", "219.90", 10),
            new ProductSeed("MET-FREN-01", "Métodos", "Prensa francesa 350 ml",
                    "Prensa francesa compacta com corpo de vidro resistente.", "149.90", 6),
            new ProductSeed("MET-CHEM-01", "Métodos", "Chemex Classic 6 xícaras",
                    "Cafeteira de vidro para extração filtrada e uniforme.", "389.90", 3),
            new ProductSeed("CAF-BRA-250", "Cafés Especiais", "Café especial do Cerrado 250 g",
                    "Café brasileiro de notas achocolatadas e baixa acidez.", "49.90", 16),
            new ProductSeed("CAF-COL-250", "Cafés Especiais", "Café especial da Colômbia 250 g",
                    "Café colombiano de torra média e notas frutadas.", "59.90", 4),
            new ProductSeed("CAF-ETH-250", "Cafés Especiais", "Café especial da Etiópia 250 g",
                    "Café etíope de perfil floral e cítrico, em grãos.", "69.90", 0),
            new ProductSeed("CAF-DESC-250", "Cafés Especiais", "Café especial descafeinado 250 g",
                    "Café descafeinado em grãos, preservando aroma e sabor.", "46.90", 9),
            new ProductSeed("KIT-CAP-ESP", "Cápsulas & Kits", "Cápsulas Espresso Intenso · 10 un.",
                    "Caixa com dez cápsulas compatíveis para espresso intenso.", "29.90", 18),
            new ProductSeed("KIT-CAP-MIX", "Cápsulas & Kits", "Kit degustação de cápsulas · 30 un.",
                    "Seleção de trinta cápsulas com diferentes perfis de torra.", "79.90", 4),
            new ProductSeed("KIT-PRES-CAF", "Cápsulas & Kits", "Kit presente café e caneca",
                    "Kit para presente com café especial e caneca cerâmica.", "124.90", 2),
            new ProductSeed("ACE-FILT-V60", "Acessórios", "Filtros de papel V60 02 · 100 un.",
                    "Pacote com cem filtros de papel tamanho 02 para V60.", "32.90", 16)
    );

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductDataInitializer(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        for (ProductSeed seed : PRODUCTS) {
            Category category = categoryRepository.findByNameIgnoreCase(seed.categoryName())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(seed.categoryName());
                        return categoryRepository.save(newCategory);
                    });

            Product product = productRepository.findBySkuIgnoreCase(seed.sku()).orElse(null);
            if (product == null) {
                product = productRepository.findFirstByNameIgnoreCase(seed.name()).orElse(null);
            }

            if (product == null) {
                product = new Product();
                product.setSku(seed.sku());
                product.setName(seed.name());
                product.setDescription(seed.description());
                product.setPrice(new BigDecimal(seed.price()));
                product.setStock(seed.stock());
                product.setCategory(category);
                productRepository.save(product);
            } else {
                boolean changed = false;
                if (isBlank(product.getSku())) {
                    product.setSku(seed.sku());
                    changed = true;
                }
                if (isBlank(product.getDescription())) {
                    product.setDescription(seed.description());
                    changed = true;
                }
                if (product.getCategory() == null
                        || !product.getCategory().getName().equalsIgnoreCase(seed.categoryName())) {
                    product.setCategory(category);
                    changed = true;
                }
                if (changed) {
                    productRepository.save(product);
                }
            }
        }

        for (Product product : productRepository.findAll()) {
            boolean changed = false;
            if (isBlank(product.getSku())) {
                product.setSku("LEGACY-" + product.getId().toString().replace("-", "").substring(0, 12));
                changed = true;
            }
            if (isBlank(product.getDescription())) {
                product.setDescription(product.getName() + " disponível no catálogo.");
                changed = true;
            }
            if (changed) {
                productRepository.save(product);
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ProductSeed(
            String sku,
            String categoryName,
            String name,
            String description,
            String price,
            int stock
    ) {
    }
}
