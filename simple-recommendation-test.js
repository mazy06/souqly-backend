const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';

// Fonction pour tester les recommandations
async function testRecommendations() {
  console.log('🧪 Test de l\'algorithme de recommandation Souqly\n');
  
  try {
    // Test 1: Vérifier la connexion
    console.log('📊 Test 1: Vérification de la connexion');
    try {
      const healthResponse = await axios.get(`${BASE_URL}/health`);
      console.log('✅ Serveur connecté:', healthResponse.data);
    } catch (error) {
      console.log('⚠️ Endpoint /health non disponible, continuons...');
    }
    
    // Test 2: Récupérer tous les produits
    console.log('\n📊 Test 2: Récupération des produits');
    const productsResponse = await axios.get(`${BASE_URL}/products`);
    const products = productsResponse.data.content || productsResponse.data; // Gérer la pagination
    console.log(`✅ ${products.length} produits trouvés`);
    
    // Afficher les produits avec leurs statistiques
    console.log('\n📋 Détails des produits:');
    products.forEach((product, index) => {
      console.log(`${index + 1}. ${product.title}`);
      console.log(`   💰 Prix: ${product.price}€ | ⭐ Favoris: ${product.favoriteCount || 0} | 👁️ Vues: ${product.viewCount || 0}`);
      console.log(`   🏷️ ${product.brand || 'N/A'} | 📍 ${product.city || 'N/A'} | 📦 ${product.condition || 'N/A'}\n`);
    });
    
    // Test 3: Tester les recommandations (si l'endpoint existe)
    console.log('\n📊 Test 3: Test des recommandations');
    try {
      const recommendationsResponse = await axios.get(`${BASE_URL}/recommendations/for-me?limit=5`);
      const recommendations = recommendationsResponse.data.content || recommendationsResponse.data;
      console.log(`✅ ${recommendations.length} recommandations trouvées`);
      
      if (recommendations.length > 0) {
        console.log('\n📋 Recommandations:');
        recommendations.forEach((product, index) => {
          console.log(`${index + 1}. ${product.title}`);
          console.log(`   💰 Prix: ${product.price}€ | ⭐ Favoris: ${product.favoriteCount || 0}`);
          console.log(`   🏷️ ${product.brand || 'N/A'} | 📍 ${product.city || 'N/A'}\n`);
        });
      }
    } catch (error) {
      console.log('⚠️ Endpoint de recommandations non disponible ou en erreur');
      console.log('   Erreur:', error.response?.data || error.message);
    }
    
    // Test 4: Analyser les données existantes
    console.log('\n📊 Test 4: Analyse des données existantes');
    const categories = {};
    const brands = {};
    const conditions = {};
    const priceRanges = { '0-50': 0, '51-100': 0, '101-200': 0, '201-500': 0, '500+': 0 };
    
    products.forEach(product => {
      // Catégories (si disponible)
      if (product.category) {
        categories[product.category] = (categories[product.category] || 0) + 1;
      }
      
      // Marques
      if (product.brand) {
        brands[product.brand] = (brands[product.brand] || 0) + 1;
      }
      
      // Conditions
      if (product.condition) {
        conditions[product.condition] = (conditions[product.condition] || 0) + 1;
      }
      
      // Fourchettes de prix
      const price = parseFloat(product.price) || 0;
      if (price <= 50) priceRanges['0-50']++;
      else if (price <= 100) priceRanges['51-100']++;
      else if (price <= 200) priceRanges['101-200']++;
      else if (price <= 500) priceRanges['201-500']++;
      else priceRanges['500+']++;
    });
    
    console.log('📈 Répartition par marque:');
    Object.entries(brands).forEach(([brand, count]) => {
      console.log(`   ${brand}: ${count} produits`);
    });
    
    console.log('\n📈 Répartition par condition:');
    Object.entries(conditions).forEach(([condition, count]) => {
      console.log(`   ${condition}: ${count} produits`);
    });
    
    console.log('\n📈 Répartition par prix:');
    Object.entries(priceRanges).forEach(([range, count]) => {
      console.log(`   ${range}€: ${count} produits`);
    });
    
    // Test 5: Simuler des recommandations basées sur les données existantes
    console.log('\n📊 Test 5: Simulation de recommandations');
    console.log('🎯 Recommandations basées sur la popularité (favoris):');
    const popularProducts = products
      .filter(p => p.favoriteCount > 0)
      .sort((a, b) => (b.favoriteCount || 0) - (a.favoriteCount || 0))
      .slice(0, 5);
    
    popularProducts.forEach((product, index) => {
      console.log(`${index + 1}. ${product.title} (${product.favoriteCount} favoris)`);
    });
    
    console.log('\n🎯 Recommandations basées sur les vues:');
    const viewedProducts = products
      .filter(p => p.viewCount > 0)
      .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      .slice(0, 5);
    
    viewedProducts.forEach((product, index) => {
      console.log(`${index + 1}. ${product.title} (${product.viewCount} vues)`);
    });
    
    console.log('\n🎯 Recommandations par prix (moyen):');
    const avgPrice = products.reduce((sum, p) => sum + (parseFloat(p.price) || 0), 0) / products.length;
    const affordableProducts = products
      .filter(p => parseFloat(p.price) <= avgPrice)
      .sort((a, b) => parseFloat(a.price) - parseFloat(b.price))
      .slice(0, 5);
    
    affordableProducts.forEach((product, index) => {
      console.log(`${index + 1}. ${product.title} (${product.price}€)`);
    });
    
    // Test 6: Analyser les produits boostés (simulation)
    console.log('\n📊 Test 6: Analyse des produits boostés (simulation)');
    const highValueProducts = products.filter(p => parseFloat(p.price) > 1000);
    const popularBrands = products.filter(p => p.brand && ['Apple', 'Audi', 'Samsung'].includes(p.brand));
    
    console.log('🔥 Produits haute valeur (>1000€):');
    highValueProducts.forEach((product, index) => {
      console.log(`   ${index + 1}. ${product.title} (${product.price}€)`);
    });
    
    console.log('\n🏷️ Produits de marques populaires:');
    popularBrands.forEach((product, index) => {
      console.log(`   ${index + 1}. ${product.title} (${product.brand})`);
    });
    
    return products;
    
  } catch (error) {
    console.error('❌ Erreur lors du test:', error.message);
    if (error.response) {
      console.error('Détails:', error.response.data);
    }
    return [];
  }
}

// Fonction pour analyser l'efficacité des algorithmes
function analyzeAlgorithmEfficiency(products) {
  console.log('\n📊 ANALYSE DE L\'EFFICACITÉ');
  console.log('============================');
  
  if (!Array.isArray(products)) {
    console.log('❌ Données de produits invalides');
    return;
  }
  
  // Calculer les métriques
  const totalProducts = products.length;
  const productsWithFavorites = products.filter(p => p.favoriteCount > 0).length;
  const productsWithViews = products.filter(p => p.viewCount > 0).length;
  const avgFavorites = products.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / totalProducts;
  const avgViews = products.reduce((sum, p) => sum + (p.viewCount || 0), 0) / totalProducts;
  const avgPrice = products.reduce((sum, p) => sum + (parseFloat(p.price) || 0), 0) / totalProducts;
  
  console.log(`📈 Métriques générales:`);
  console.log(`   - Total produits: ${totalProducts}`);
  console.log(`   - Produits avec favoris: ${productsWithFavorites} (${(productsWithFavorites/totalProducts*100).toFixed(1)}%)`);
  console.log(`   - Produits avec vues: ${productsWithViews} (${(productsWithViews/totalProducts*100).toFixed(1)}%)`);
  console.log(`   - Favoris moyens: ${avgFavorites.toFixed(1)}`);
  console.log(`   - Vues moyennes: ${avgViews.toFixed(1)}`);
  console.log(`   - Prix moyen: ${avgPrice.toFixed(1)}€`);
  
  // Recommandations pour améliorer l'algorithme
  console.log(`\n💡 Recommandations pour l'algorithme:`);
  if (avgFavorites < 2) {
    console.log(`   - ⚠️ Peu de favoris moyens (${avgFavorites.toFixed(1)}), considérer d'autres métriques`);
  }
  if (avgViews < 10) {
    console.log(`   - ⚠️ Peu de vues moyennes (${avgViews.toFixed(1)}), améliorer la visibilité`);
  }
  if (productsWithFavorites / totalProducts < 0.3) {
    console.log(`   - ⚠️ Faible taux d'engagement (${(productsWithFavorites/totalProducts*100).toFixed(1)}%), optimiser l'UX`);
  }
  
  console.log(`   - ✅ Utiliser un algorithme hybride (content-based + collaborative)`);
  console.log(`   - ✅ Implémenter des boosts pour les produits populaires`);
  console.log(`   - ✅ Ajouter des filtres par catégorie/prix/localisation`);
  console.log(`   - ✅ Considérer le prix moyen (${avgPrice.toFixed(1)}€) pour les recommandations`);
}

// Fonction pour simuler l'algorithme de recommandation
function simulateRecommendationAlgorithm(products, userId = 1) {
  console.log('\n🎯 SIMULATION ALGORITHME DE RECOMMANDATION');
  console.log('==========================================');
  
  // Algorithme simple basé sur plusieurs critères
  const scoredProducts = products.map(product => {
    let score = 0;
    
    // Score basé sur les favoris (poids: 0.4)
    score += (product.favoriteCount || 0) * 0.4;
    
    // Score basé sur les vues (poids: 0.3)
    score += (product.viewCount || 0) * 0.3;
    
    // Score basé sur le prix (poids: 0.2) - préférer les prix moyens
    const price = parseFloat(product.price) || 0;
    const avgPrice = products.reduce((sum, p) => sum + (parseFloat(p.price) || 0), 0) / products.length;
    const priceScore = Math.max(0, 1 - Math.abs(price - avgPrice) / avgPrice);
    score += priceScore * 0.2;
    
    // Score basé sur la marque (poids: 0.1) - préférer les marques populaires
    const popularBrands = ['Apple', 'Audi', 'Samsung', 'Nike', 'Adidas'];
    if (product.brand && popularBrands.includes(product.brand)) {
      score += 0.1;
    }
    
    return { ...product, recommendationScore: score };
  });
  
  // Trier par score de recommandation
  const recommendations = scoredProducts
    .sort((a, b) => b.recommendationScore - a.recommendationScore)
    .slice(0, 5);
  
  console.log('🏆 Top 5 recommandations simulées:');
  recommendations.forEach((product, index) => {
    console.log(`${index + 1}. ${product.title}`);
    console.log(`   💰 Prix: ${product.price}€ | ⭐ Favoris: ${product.favoriteCount || 0} | 👁️ Vues: ${product.viewCount || 0}`);
    console.log(`   🏷️ ${product.brand || 'N/A'} | 📊 Score: ${product.recommendationScore.toFixed(2)}\n`);
  });
  
  return recommendations;
}

// Fonction principale
async function main() {
  try {
    const products = await testRecommendations();
    
    if (products.length > 0) {
      analyzeAlgorithmEfficiency(products);
      simulateRecommendationAlgorithm(products);
    }
    
    console.log('\n🎉 Tests terminés !');
    console.log('\n📋 Prochaines étapes:');
    console.log('   1. Implémenter l\'endpoint de recommandations');
    console.log('   2. Ajouter des données de test plus complètes');
    console.log('   3. Tester avec différents utilisateurs');
    console.log('   4. Optimiser les performances');
    console.log('   5. Implémenter le système de boost');
    
  } catch (error) {
    console.error('❌ Erreur:', error.message);
  }
}

// Exécuter les tests
if (require.main === module) {
  main().catch(console.error);
}

module.exports = {
  testRecommendations,
  analyzeAlgorithmEfficiency,
  simulateRecommendationAlgorithm
}; 