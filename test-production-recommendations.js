const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';

// Fonction pour tester les endpoints de recommandations
async function testRecommendationEndpoints() {
  console.log('🧪 Test des endpoints de recommandations en production\n');
  
  const endpoints = [
    {
      name: 'Produits (référence)',
      url: '/products',
      method: 'GET'
    },
    {
      name: 'Recommandations pour moi',
      url: '/recommendations/for-me?limit=5',
      method: 'GET'
    },
    {
      name: 'Recommandations test',
      url: '/recommendations/test?limit=5',
      method: 'GET'
    },
    {
      name: 'Recommandations content-based',
      url: '/recommendations/content-based/1?limit=5',
      method: 'GET'
    },
    {
      name: 'Recommandations hybrides',
      url: '/recommendations/hybrid/1?limit=5',
      method: 'GET'
    }
  ];
  
  for (const endpoint of endpoints) {
    try {
      console.log(`📊 Test: ${endpoint.name}`);
      console.log(`   URL: ${endpoint.url}`);
      
      const startTime = Date.now();
      const response = await axios.get(`${BASE_URL}${endpoint.url}`);
      const endTime = Date.now();
      
      console.log(`   ✅ Status: ${response.status}`);
      console.log(`   ⏱️ Temps: ${endTime - startTime}ms`);
      
      if (response.data) {
        if (Array.isArray(response.data)) {
          console.log(`   📦 Données: ${response.data.length} éléments`);
        } else if (response.data.content) {
          console.log(`   📦 Données: ${response.data.content.length} éléments (paginé)`);
        } else if (response.data.recommendations) {
          console.log(`   📦 Recommandations: ${response.data.recommendations.length} éléments`);
          if (response.data.metrics) {
            console.log(`   📊 Métriques: ${JSON.stringify(response.data.metrics)}`);
          }
        } else {
          console.log(`   📦 Données: ${typeof response.data}`);
        }
      }
      
      console.log('');
      
    } catch (error) {
      console.log(`   ❌ Erreur: ${error.response?.status || error.code}`);
      console.log(`   📝 Message: ${error.response?.data || error.message}`);
      console.log('');
    }
  }
}

// Fonction pour tester les services de recommandation
async function testRecommendationServices() {
  console.log('🔧 Test des services de recommandation\n');
  
  try {
    // Test 1: Vérifier les produits avec boost
    console.log('📊 Test 1: Vérification des produits avec boost');
    const productsResponse = await axios.get(`${BASE_URL}/products`);
    const products = productsResponse.data.content || productsResponse.data;
    
    const boostedProducts = products.filter(p => p.isBoosted);
    const productsWithBoostLevel = products.filter(p => p.boostLevel > 0);
    
    console.log(`   Total produits: ${products.length}`);
    console.log(`   Produits boostés: ${boostedProducts.length}`);
    console.log(`   Produits avec boost level: ${productsWithBoostLevel.length}`);
    
    // Afficher quelques produits boostés
    if (boostedProducts.length > 0) {
      console.log('   🔥 Produits boostés:');
      boostedProducts.slice(0, 3).forEach(product => {
        console.log(`     - ${product.title} (boost level: ${product.boostLevel})`);
      });
    }
    
    console.log('');
    
    // Test 2: Simuler des recommandations
    console.log('📊 Test 2: Simulation de recommandations');
    const popularProducts = products
      .filter(p => p.favoriteCount > 0)
      .sort((a, b) => (b.favoriteCount || 0) - (a.favoriteCount || 0))
      .slice(0, 5);
    
    console.log(`   Top 5 produits populaires:`);
    popularProducts.forEach((product, index) => {
      const boostInfo = product.isBoosted ? `🔥 Boosté (niveau ${product.boostLevel})` : '📦 Standard';
      console.log(`     ${index + 1}. ${product.title} (${product.favoriteCount} favoris) - ${boostInfo}`);
    });
    
    console.log('');
    
    // Test 3: Calculer les métriques
    console.log('📊 Test 3: Calcul des métriques');
    const totalProducts = products.length;
    const avgFavorites = products.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / totalProducts;
    const avgPrice = products.reduce((sum, p) => sum + (p.price || 0), 0) / totalProducts;
    const uniqueBrands = [...new Set(products.map(p => p.brand).filter(Boolean))];
    
    console.log(`   Favoris moyens: ${avgFavorites.toFixed(1)}`);
    console.log(`   Prix moyen: ${avgPrice.toFixed(1)}€`);
    console.log(`   Marques uniques: ${uniqueBrands.length}`);
    console.log(`   Diversité: ${(uniqueBrands.length / totalProducts * 100).toFixed(1)}%`);
    
    console.log('');
    
  } catch (error) {
    console.error('❌ Erreur lors du test des services:', error.message);
  }
}

// Fonction pour tester l'intégration frontend
async function testFrontendIntegration() {
  console.log('🎨 Test de l\'intégration frontend\n');
  
  try {
    // Simuler l'appel frontend
    const frontendRecommendations = await axios.get(`${BASE_URL}/recommendations/for-me?limit=5&includeMetrics=true`);
    
    console.log('✅ Endpoint frontend accessible');
    
    if (frontendRecommendations.data.recommendations) {
      console.log(`📦 ${frontendRecommendations.data.recommendations.length} recommandations`);
      
      if (frontendRecommendations.data.metrics) {
        console.log('📊 Métriques disponibles:');
        Object.entries(frontendRecommendations.data.metrics).forEach(([key, value]) => {
          console.log(`   ${key}: ${value}`);
        });
      }
    }
    
    console.log('');
    
  } catch (error) {
    console.log('❌ Endpoint frontend non accessible');
    console.log(`   Erreur: ${error.response?.status || error.code}`);
    console.log('');
  }
}

// Fonction principale
async function main() {
  console.log('🚀 Test des recommandations en production\n');
  
  try {
    // Test 1: Endpoints
    await testRecommendationEndpoints();
    
    // Test 2: Services
    await testRecommendationServices();
    
    // Test 3: Intégration frontend
    await testFrontendIntegration();
    
    console.log('🎉 Tests terminés !');
    console.log('\n📋 Prochaines étapes:');
    console.log('   1. Vérifier les logs du serveur pour les erreurs');
    console.log('   2. Tester avec des données de boost réelles');
    console.log('   3. Intégrer dans le frontend');
    console.log('   4. Optimiser les performances');
    
  } catch (error) {
    console.error('❌ Erreur générale:', error.message);
  }
}

// Exécuter les tests
if (require.main === module) {
  main().catch(console.error);
}

module.exports = {
  testRecommendationEndpoints,
  testRecommendationServices,
  testFrontendIntegration
}; 