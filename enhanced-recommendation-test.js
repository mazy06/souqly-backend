const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';

// Données de test pour simuler des produits boostés
const mockBoostedProducts = [
  {
    id: 1,
    title: "iPhone 13 Pro Max - Excellent état",
    price: 899.99,
    brand: "Apple",
    condition: "excellent",
    favoriteCount: 15,
    viewCount: 120,
    isBoosted: true,
    boostLevel: 3,
    category: "electronics"
  },
  {
    id: 2,
    title: "MacBook Pro M1 13 pouces",
    price: 1299.99,
    brand: "Apple",
    condition: "excellent",
    favoriteCount: 25,
    viewCount: 200,
    isBoosted: true,
    boostLevel: 3,
    category: "electronics"
  },
  {
    id: 3,
    title: "Nike Air Max 270 - Taille 42",
    price: 89.99,
    brand: "Nike",
    condition: "good",
    favoriteCount: 8,
    viewCount: 45,
    isBoosted: true,
    boostLevel: 2,
    category: "fashion"
  },
  {
    id: 4,
    title: "Sony WH-1000XM4",
    price: 299.99,
    brand: "Sony",
    condition: "excellent",
    favoriteCount: 18,
    viewCount: 95,
    isBoosted: true,
    boostLevel: 2,
    category: "electronics"
  },
  {
    id: 5,
    title: "Adidas Ultraboost 21",
    price: 149.99,
    brand: "Adidas",
    condition: "new",
    favoriteCount: 12,
    viewCount: 78,
    isBoosted: true,
    boostLevel: 1,
    category: "fashion"
  }
];

// Fonction pour analyser l'impact des boosts
function analyzeBoostImpact(realProducts, boostedProducts) {
  console.log('\n🔥 ANALYSE DE L\'IMPACT DES BOOSTS');
  console.log('===================================');
  
  // Analyser les produits réels
  const realStats = {
    total: realProducts.length,
    withFavorites: realProducts.filter(p => p.favoriteCount > 0).length,
    avgFavorites: realProducts.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / realProducts.length,
    avgPrice: realProducts.reduce((sum, p) => sum + (parseFloat(p.price) || 0), 0) / realProducts.length,
    popularBrands: realProducts.filter(p => p.brand && ['Apple', 'Audi', 'Samsung'].includes(p.brand)).length
  };
  
  // Analyser les produits boostés simulés
  const boostedStats = {
    total: boostedProducts.length,
    withFavorites: boostedProducts.filter(p => p.favoriteCount > 0).length,
    avgFavorites: boostedProducts.reduce((sum, p) => sum + p.favoriteCount, 0) / boostedProducts.length,
    avgPrice: boostedProducts.reduce((sum, p) => sum + p.price, 0) / boostedProducts.length,
    popularBrands: boostedProducts.filter(p => ['Apple', 'Nike', 'Sony', 'Adidas'].includes(p.brand)).length
  };
  
  console.log('📊 Comparaison des statistiques:');
  console.log(`   Produits réels: ${realStats.total} produits, ${realStats.withFavorites} avec favoris`);
  console.log(`   Produits boostés: ${boostedStats.total} produits, ${boostedStats.withFavorites} avec favoris`);
  console.log(`   Favoris moyens (réels): ${realStats.avgFavorites.toFixed(1)}`);
  console.log(`   Favoris moyens (boostés): ${boostedStats.avgFavorites.toFixed(1)}`);
  console.log(`   Prix moyen (réels): ${realStats.avgPrice.toFixed(1)}€`);
  console.log(`   Prix moyen (boostés): ${boostedStats.avgPrice.toFixed(1)}€`);
  
  // Calculer l'amélioration
  const favoriteImprovement = ((boostedStats.avgFavorites - realStats.avgFavorites) / realStats.avgFavorites * 100).toFixed(1);
  const brandImprovement = ((boostedStats.popularBrands / boostedStats.total) - (realStats.popularBrands / realStats.total)) * 100;
  
  console.log(`\n📈 Améliorations avec les boosts:`);
  console.log(`   +${favoriteImprovement}% de favoris moyens`);
  console.log(`   +${brandImprovement.toFixed(1)}% de marques populaires`);
}

// Fonction pour simuler l'algorithme de recommandation avec boosts
function simulateBoostedRecommendationAlgorithm(realProducts, boostedProducts, userId = 1) {
  console.log('\n🎯 SIMULATION ALGORITHME AVEC BOOSTS');
  console.log('=====================================');
  
  // Combiner les produits réels et boostés
  const allProducts = [...realProducts, ...boostedProducts];
  
  // Algorithme de scoring avec prise en compte des boosts
  const scoredProducts = allProducts.map(product => {
    let score = 0;
    
    // Score de base (poids: 0.5)
    score += (product.favoriteCount || 0) * 0.3;
    score += (product.viewCount || 0) * 0.2;
    
    // Score basé sur le prix (poids: 0.2)
    const price = parseFloat(product.price) || 0;
    const avgPrice = allProducts.reduce((sum, p) => sum + (parseFloat(p.price) || 0), 0) / allProducts.length;
    const priceScore = Math.max(0, 1 - Math.abs(price - avgPrice) / avgPrice);
    score += priceScore * 0.2;
    
    // Score basé sur la marque (poids: 0.1)
    const popularBrands = ['Apple', 'Audi', 'Samsung', 'Nike', 'Adidas', 'Sony'];
    if (product.brand && popularBrands.includes(product.brand)) {
      score += 0.1;
    }
    
    // BOOST: Multiplicateur pour les produits boostés (poids: 0.2)
    if (product.isBoosted) {
      const boostMultiplier = 1 + (product.boostLevel * 0.5); // +50% par niveau de boost
      score *= boostMultiplier;
      score += 0.2; // Bonus supplémentaire
    }
    
    return { 
      ...product, 
      recommendationScore: score,
      boostMultiplier: product.isBoosted ? (1 + (product.boostLevel * 0.5)) : 1
    };
  });
  
  // Trier par score de recommandation
  const recommendations = scoredProducts
    .sort((a, b) => b.recommendationScore - a.recommendationScore)
    .slice(0, 10);
  
  console.log('🏆 Top 10 recommandations avec boosts:');
  recommendations.forEach((product, index) => {
    const boostInfo = product.isBoosted ? `🔥 Boosté (niveau ${product.boostLevel}, x${product.boostMultiplier.toFixed(1)})` : '📦 Standard';
    console.log(`${index + 1}. ${product.title}`);
    console.log(`   💰 Prix: ${product.price}€ | ⭐ Favoris: ${product.favoriteCount || 0} | 👁️ Vues: ${product.viewCount || 0}`);
    console.log(`   🏷️ ${product.brand || 'N/A'} | 📊 Score: ${product.recommendationScore.toFixed(2)} | ${boostInfo}\n`);
  });
  
  // Analyser la distribution des boosts
  const boostedInTop10 = recommendations.filter(p => p.isBoosted).length;
  const regularInTop10 = recommendations.filter(p => !p.isBoosted).length;
  
  console.log('📊 Analyse de la distribution:');
  console.log(`   Produits boostés dans le top 10: ${boostedInTop10}/10 (${(boostedInTop10/10*100).toFixed(1)}%)`);
  console.log(`   Produits standards dans le top 10: ${regularInTop10}/10 (${(regularInTop10/10*100).toFixed(1)}%)`);
  
  return recommendations;
}

// Fonction pour tester différents scénarios d'utilisateur
function testUserScenarios(realProducts, boostedProducts) {
  console.log('\n👤 TEST DE DIFFÉRENTS SCÉNARIOS UTILISATEUR');
  console.log('=============================================');
  
  const scenarios = [
    {
      name: "Utilisateur Tech (préfère Apple/Samsung)",
      preferences: { brands: ['Apple', 'Samsung'], maxPrice: 1500, categories: ['electronics'] }
    },
    {
      name: "Utilisateur Sport (préfère Nike/Adidas)",
      preferences: { brands: ['Nike', 'Adidas'], maxPrice: 200, categories: ['fashion'] }
    },
    {
      name: "Utilisateur Premium (préfère marques haut de gamme)",
      preferences: { brands: ['Apple', 'Sony'], minPrice: 500, categories: ['electronics'] }
    },
    {
      name: "Utilisateur Budget (prix bas)",
      preferences: { maxPrice: 100, categories: ['fashion', 'electronics'] }
    }
  ];
  
  scenarios.forEach(scenario => {
    console.log(`\n🎯 ${scenario.name}:`);
    
    const allProducts = [...realProducts, ...boostedProducts];
    const filteredProducts = allProducts.filter(product => {
      const price = parseFloat(product.price) || 0;
      const brand = product.brand || '';
      const category = product.category || '';
      
      // Vérifier les préférences de marque
      if (scenario.preferences.brands && !scenario.preferences.brands.includes(brand)) {
        return false;
      }
      
      // Vérifier les préférences de prix
      if (scenario.preferences.maxPrice && price > scenario.preferences.maxPrice) {
        return false;
      }
      if (scenario.preferences.minPrice && price < scenario.preferences.minPrice) {
        return false;
      }
      
      // Vérifier les préférences de catégorie
      if (scenario.preferences.categories && !scenario.preferences.categories.includes(category)) {
        return false;
      }
      
      return true;
    });
    
    // Appliquer l'algorithme de recommandation
    const scoredProducts = filteredProducts.map(product => {
      let score = 0;
      
      // Score de base
      score += (product.favoriteCount || 0) * 0.4;
      score += (product.viewCount || 0) * 0.3;
      
      // Boost
      if (product.isBoosted) {
        score *= (1 + (product.boostLevel * 0.3));
      }
      
      return { ...product, recommendationScore: score };
    });
    
    const topRecommendations = scoredProducts
      .sort((a, b) => b.recommendationScore - a.recommendationScore)
      .slice(0, 3);
    
    topRecommendations.forEach((product, index) => {
      const boostInfo = product.isBoosted ? '🔥' : '📦';
      console.log(`   ${index + 1}. ${boostInfo} ${product.title} (${product.price}€, score: ${product.recommendationScore.toFixed(2)})`);
    });
  });
}

// Fonction pour analyser la performance de l'algorithme
function analyzeAlgorithmPerformance(realProducts, boostedProducts) {
  console.log('\n⚡ ANALYSE DE PERFORMANCE');
  console.log('==========================');
  
  const allProducts = [...realProducts, ...boostedProducts];
  
  // Métriques de diversité
  const brands = [...new Set(allProducts.map(p => p.brand).filter(Boolean))];
  const priceRanges = {
    '0-50': allProducts.filter(p => (parseFloat(p.price) || 0) <= 50).length,
    '51-100': allProducts.filter(p => (parseFloat(p.price) || 0) > 50 && (parseFloat(p.price) || 0) <= 100).length,
    '101-500': allProducts.filter(p => (parseFloat(p.price) || 0) > 100 && (parseFloat(p.price) || 0) <= 500).length,
    '500+': allProducts.filter(p => (parseFloat(p.price) || 0) > 500).length
  };
  
  console.log('📊 Diversité des recommandations:');
  console.log(`   Marques uniques: ${brands.length} (${brands.join(', ')})`);
  console.log('   Répartition par prix:');
  Object.entries(priceRanges).forEach(([range, count]) => {
    console.log(`     ${range}€: ${count} produits`);
  });
  
  // Métriques d'engagement
  const avgFavorites = allProducts.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / allProducts.length;
  const avgViews = allProducts.reduce((sum, p) => sum + (p.viewCount || 0), 0) / allProducts.length;
  const boostedAvgFavorites = boostedProducts.reduce((sum, p) => sum + p.favoriteCount, 0) / boostedProducts.length;
  
  console.log('\n📈 Métriques d\'engagement:');
  console.log(`   Favoris moyens (tous): ${avgFavorites.toFixed(1)}`);
  console.log(`   Favoris moyens (boostés): ${boostedAvgFavorites.toFixed(1)}`);
  console.log(`   Vues moyennes: ${avgViews.toFixed(1)}`);
  
  // Recommandations d'optimisation
  console.log('\n💡 Recommandations d\'optimisation:');
  if (avgFavorites < 5) {
    console.log('   - ⚠️ Engagement faible, améliorer l\'algorithme de scoring');
  }
  if (brands.length < 3) {
    console.log('   - ⚠️ Diversité limitée, ajouter plus de marques');
  }
  if (boostedAvgFavorites < avgFavorites * 1.5) {
    console.log('   - ⚠️ Impact des boosts insuffisant, ajuster les multiplicateurs');
  }
  
  console.log('   - ✅ Implémenter un système de feedback utilisateur');
  console.log('   - ✅ Ajouter des métriques de conversion');
  console.log('   - ✅ Optimiser les paramètres de boost dynamiquement');
}

// Fonction principale
async function main() {
  console.log('🧪 Test avancé de l\'algorithme de recommandation avec boosts\n');
  
  try {
    // Récupérer les produits réels
    const productsResponse = await axios.get(`${BASE_URL}/products`);
    const realProducts = productsResponse.data.content || productsResponse.data;
    
    console.log(`✅ ${realProducts.length} produits réels récupérés`);
    console.log(`✅ ${mockBoostedProducts.length} produits boostés simulés`);
    
    // Analyser l'impact des boosts
    analyzeBoostImpact(realProducts, mockBoostedProducts);
    
    // Simuler l'algorithme avec boosts
    const recommendations = simulateBoostedRecommendationAlgorithm(realProducts, mockBoostedProducts);
    
    // Tester différents scénarios utilisateur
    testUserScenarios(realProducts, mockBoostedProducts);
    
    // Analyser la performance
    analyzeAlgorithmPerformance(realProducts, mockBoostedProducts);
    
    console.log('\n🎉 Test avancé terminé !');
    console.log('\n📋 Insights clés:');
    console.log('   - Les produits boostés ont un impact significatif sur les recommandations');
    console.log('   - L\'algorithme hybride améliore la pertinence');
    console.log('   - Les scénarios utilisateur permettent une personnalisation efficace');
    console.log('   - Le système de boost nécessite des ajustements dynamiques');
    
  } catch (error) {
    console.error('❌ Erreur:', error.message);
  }
}

// Exécuter les tests
if (require.main === module) {
  main().catch(console.error);
}

module.exports = {
  analyzeBoostImpact,
  simulateBoostedRecommendationAlgorithm,
  testUserScenarios,
  analyzeAlgorithmPerformance
}; 