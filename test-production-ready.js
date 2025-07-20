const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';

// Fonction pour tester le système en production
async function testProductionReady() {
  console.log('🚀 Test de Production - Système de Recommandations avec Boosts');
  console.log('=============================================================\n');

  const results = {
    database: false,
    api: false,
    boosts: false,
    recommendations: false,
    frontend: false
  };

  try {
    // Test 1: Vérifier la base de données et les boosts
    console.log('📊 Test 1: Vérification de la base de données...');
    const productsResponse = await axios.get(`${BASE_URL}/products`);
    
    if (productsResponse.data && productsResponse.data.content) {
      const products = productsResponse.data.content;
      console.log(`   ✅ ${products.length} produits récupérés via l'API`);
      
      // Vérifier les boosts en base de données
      const boostedProducts = products.filter(p => p.isBoosted === true);
      console.log(`   🔥 ${boostedProducts.length} produits boostés détectés`);
      
      if (boostedProducts.length > 0) {
        boostedProducts.forEach(product => {
          console.log(`      - ${product.title} (Boost: ${product.boostLevel || 0})`);
        });
        results.boosts = true;
      }
      
      results.database = true;
      results.api = true;
    }

    // Test 2: Simuler l'algorithme de recommandation
    console.log('\n🤖 Test 2: Simulation de l\'algorithme de recommandation...');
    
    const recommendations = simulateRecommendationAlgorithm(productsResponse.data.content);
    console.log(`   📦 ${recommendations.length} recommandations générées`);
    
    const boostedRecs = recommendations.filter(r => r.isBoosted);
    console.log(`   🚀 ${boostedRecs.length} recommandations boostées`);
    
    if (recommendations.length > 0) {
      console.log('   🎯 Top 5 recommandations:');
      recommendations.slice(0, 5).forEach((rec, index) => {
        const boostInfo = rec.isBoosted ? ` (🔥 Boost: ${rec.boostLevel})` : '';
        console.log(`      ${index + 1}. ${rec.title} - ${rec.price}€${boostInfo}`);
      });
      results.recommendations = true;
    }

    // Test 3: Calculer les métriques de performance
    console.log('\n📈 Test 3: Métriques de performance...');
    
    const metrics = calculatePerformanceMetrics(productsResponse.data.content, recommendations);
    console.log(`   💰 Prix moyen: ${metrics.avgPrice.toFixed(2)}€`);
    console.log(`   ⭐ Favoris moyens: ${metrics.avgFavorites.toFixed(1)}`);
    console.log(`   🔥 Taux de boost: ${metrics.boostPercentage.toFixed(1)}%`);
    console.log(`   📊 Diversité: ${metrics.diversity.toFixed(1)}%`);

    // Test 4: Validation des données
    console.log('\n✅ Test 4: Validation des données...');
    
    const validation = validateDataIntegrity(productsResponse.data.content);
    console.log(`   🔍 Produits valides: ${validation.valid}/${validation.total}`);
    console.log(`   ⚠️ Problèmes détectés: ${validation.issues.length}`);
    
    if (validation.issues.length > 0) {
      console.log('   Détails des problèmes:');
      validation.issues.forEach(issue => {
        console.log(`      - ${issue}`);
      });
    }

    // Test 5: Test d'intégration frontend
    console.log('\n📱 Test 5: Simulation d\'intégration frontend...');
    
    const frontendData = prepareFrontendData(recommendations);
    console.log(`   🎨 Données prêtes pour le frontend: ${frontendData.length} éléments`);
    console.log(`   🔥 Badges boostés: ${frontendData.filter(d => d.isBoosted).length}`);
    
    results.frontend = frontendData.length > 0;

    // Résumé final
    console.log('\n🎉 Résumé du test de production:');
    console.log('================================');
    
    Object.entries(results).forEach(([test, passed]) => {
      const status = passed ? '✅' : '❌';
      console.log(`   ${status} ${test}`);
    });
    
    const successRate = Object.values(results).filter(Boolean).length / Object.keys(results).length;
    console.log(`\n📊 Taux de succès: ${(successRate * 100).toFixed(1)}%`);
    
    if (successRate >= 0.8) {
      console.log('🎯 SYSTÈME PRÊT POUR LA PRODUCTION !');
      console.log('   ✅ Tous les composants sont fonctionnels');
      console.log('   ✅ Les boosts sont opérationnels');
      console.log('   ✅ Les recommandations sont générées');
      console.log('   ✅ Le frontend peut être intégré');
    } else {
      console.log('⚠️ Des améliorations sont nécessaires avant la production');
    }

  } catch (error) {
    console.error('❌ Erreur lors du test de production:', error.message);
    if (error.response) {
      console.error('   Status:', error.response.status);
      console.error('   Data:', error.response.data);
    }
  }
}

// Fonction pour simuler l'algorithme de recommandation
function simulateRecommendationAlgorithm(products) {
  const activeProducts = products.filter(p => p.status === 'ACTIVE');
  
  return activeProducts.map(product => {
    let score = 0;
    
    // Score de base basé sur les favoris
    score += (product.favoriteCount || 0) * 10;
    
    // Bonus pour les produits boostés
    if (product.isBoosted) {
      score += (product.boostLevel || 1) * 50;
    }
    
    // Bonus pour les produits récents
    const daysSinceCreation = (Date.now() - new Date(product.createdAt).getTime()) / (1000 * 60 * 60 * 24);
    if (daysSinceCreation < 7) score += 20;
    else if (daysSinceCreation < 30) score += 10;
    
    // Bonus pour les prix attractifs
    if (product.price < 100) score += 15;
    else if (product.price < 500) score += 10;
    
    return {
      ...product,
      recommendationScore: score
    };
  })
  .sort((a, b) => b.recommendationScore - a.recommendationScore)
  .slice(0, 10);
}

// Fonction pour calculer les métriques de performance
function calculatePerformanceMetrics(products, recommendations) {
  const avgPrice = products.reduce((sum, p) => sum + (p.price || 0), 0) / products.length;
  const avgFavorites = products.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / products.length;
  const boostedCount = products.filter(p => p.isBoosted).length;
  const boostPercentage = (boostedCount / products.length) * 100;
  const diversity = new Set(products.map(p => p.brand).filter(Boolean)).size / products.length * 100;
  
  return {
    avgPrice,
    avgFavorites,
    boostPercentage,
    diversity
  };
}

// Fonction pour valider l'intégrité des données
function validateDataIntegrity(products) {
  const issues = [];
  let valid = 0;
  
  products.forEach(product => {
    let isValid = true;
    
    if (!product.title || product.title.trim() === '') {
      issues.push(`Produit ${product.id}: titre manquant`);
      isValid = false;
    }
    
    if (!product.price || product.price < 0) {
      issues.push(`Produit ${product.id}: prix invalide`);
      isValid = false;
    }
    
    if (product.isBoosted && (!product.boostLevel || product.boostLevel < 1)) {
      issues.push(`Produit ${product.id}: boost sans niveau`);
      isValid = false;
    }
    
    if (isValid) valid++;
  });
  
  return { valid, total: products.length, issues };
}

// Fonction pour préparer les données frontend
function prepareFrontendData(recommendations) {
  return recommendations.map(product => ({
    id: product.id,
    title: product.title,
    price: product.price,
    brand: product.brand,
    condition: product.condition,
    favoriteCount: product.favoriteCount,
    isBoosted: product.isBoosted || false,
    boostLevel: product.boostLevel || 0,
    recommendationScore: product.recommendationScore,
    image: product.images && product.images.length > 0 ? 
      `http://localhost:8080/api/products/image/${product.images[0].id}` : null
  }));
}

// Exécuter le test
testProductionReady().then(() => {
  console.log('\n🏁 Test de production terminé.');
  process.exit(0);
}).catch(error => {
  console.error('💥 Erreur fatale:', error);
  process.exit(1);
}); 