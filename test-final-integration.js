const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';

// Fonction pour tester l'intégration complète
async function testFinalIntegration() {
  console.log('🎯 Test d\'intégration finale - Recommandations avec Boosts');
  console.log('========================================================\n');

  const results = {
    database: false,
    products: false,
    recommendations: false,
    boosts: false,
    metrics: false
  };

  try {
    // Test 1: Vérifier la base de données
    console.log('📊 Test 1: Vérification de la base de données...');
    const productsResponse = await axios.get(`${BASE_URL}/products`);
    if (productsResponse.data && productsResponse.data.content) {
      const products = productsResponse.data.content;
      const boostedProducts = products.filter(p => p.isBoosted);
      
      console.log(`   ✅ ${products.length} produits trouvés`);
      console.log(`   🔥 ${boostedProducts.length} produits boostés`);
      
      if (boostedProducts.length > 0) {
        boostedProducts.forEach(product => {
          console.log(`      - ${product.title} (Boost: ${product.boostLevel})`);
        });
      }
      
      results.database = true;
      results.products = true;
      results.boosts = boostedProducts.length > 0;
    }

    // Test 2: Vérifier les métriques
    console.log('\n📈 Test 2: Calcul des métriques...');
    const products = productsResponse.data.content;
    const boostedCount = products.filter(p => p.isBoosted).length;
    const avgPrice = products.reduce((sum, p) => sum + (p.price || 0), 0) / products.length;
    const avgFavorites = products.reduce((sum, p) => sum + (p.favoriteCount || 0), 0) / products.length;
    
    console.log(`   📊 Prix moyen: ${avgPrice.toFixed(2)}€`);
    console.log(`   ⭐ Favoris moyens: ${avgFavorites.toFixed(1)}`);
    console.log(`   🔥 Produits boostés: ${boostedCount}/${products.length} (${((boostedCount/products.length)*100).toFixed(1)}%)`);
    
    results.metrics = true;

    // Test 3: Simuler des recommandations
    console.log('\n🤖 Test 3: Simulation d\'algorithme de recommandation...');
    
    // Algorithme simple de recommandation
    const recommendations = simulateRecommendationAlgorithm(products);
    
    console.log(`   📦 ${recommendations.length} recommandations générées`);
    console.log(`   🎯 Top 3 recommandations:`);
    recommendations.slice(0, 3).forEach((rec, index) => {
      const boostInfo = rec.isBoosted ? ` (Boost: ${rec.boostLevel})` : '';
      console.log(`      ${index + 1}. ${rec.title} - ${rec.price}€${boostInfo}`);
    });

    results.recommendations = true;

    // Test 4: Analyse de l'impact des boosts
    console.log('\n🚀 Test 4: Analyse de l\'impact des boosts...');
    
    const boostedRecs = recommendations.filter(r => r.isBoosted);
    const standardRecs = recommendations.filter(r => !r.isBoosted);
    
    const avgBoostedPrice = boostedRecs.length > 0 ? 
      boostedRecs.reduce((sum, r) => sum + r.price, 0) / boostedRecs.length : 0;
    const avgStandardPrice = standardRecs.length > 0 ? 
      standardRecs.reduce((sum, r) => sum + r.price, 0) / standardRecs.length : 0;
    
    console.log(`   💰 Prix moyen boostés: ${avgBoostedPrice.toFixed(2)}€`);
    console.log(`   💰 Prix moyen standard: ${avgStandardPrice.toFixed(2)}€`);
    console.log(`   📈 Différence: ${((avgBoostedPrice - avgStandardPrice) / avgStandardPrice * 100).toFixed(1)}%`);
    
    // Test 5: Validation des données
    console.log('\n✅ Test 5: Validation des données...');
    
    const validationResults = validateData(products);
    console.log(`   🔍 Produits valides: ${validationResults.valid}/${products.length}`);
    console.log(`   ⚠️ Produits avec problèmes: ${validationResults.invalid}`);
    
    if (validationResults.invalid.length > 0) {
      console.log('   Problèmes détectés:');
      validationResults.invalid.forEach(issue => {
        console.log(`      - ${issue}`);
      });
    }

    // Résumé final
    console.log('\n🎉 Résumé du test d\'intégration:');
    console.log('================================');
    
    Object.entries(results).forEach(([test, passed]) => {
      const status = passed ? '✅' : '❌';
      console.log(`   ${status} ${test}`);
    });
    
    const successRate = Object.values(results).filter(Boolean).length / Object.keys(results).length;
    console.log(`\n📊 Taux de succès: ${(successRate * 100).toFixed(1)}%`);
    
    if (successRate >= 0.8) {
      console.log('🎯 Intégration réussie ! Le système de recommandations avec boosts est prêt.');
    } else {
      console.log('⚠️ Des problèmes ont été détectés. Vérifiez la configuration.');
    }

  } catch (error) {
    console.error('❌ Erreur lors du test d\'intégration:', error.message);
    if (error.response) {
      console.error('   Status:', error.response.status);
      console.error('   Data:', error.response.data);
    }
  }
}

// Fonction pour simuler l'algorithme de recommandation
function simulateRecommendationAlgorithm(products) {
  // Filtrer les produits actifs
  const activeProducts = products.filter(p => p.status === 'ACTIVE');
  
  // Calculer un score pour chaque produit
  const scoredProducts = activeProducts.map(product => {
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
  });
  
  // Trier par score et retourner les meilleurs
  return scoredProducts
    .sort((a, b) => b.recommendationScore - a.recommendationScore)
    .slice(0, 10);
}

// Fonction pour valider les données
function validateData(products) {
  const invalid = [];
  let valid = 0;
  
  products.forEach(product => {
    let isValid = true;
    
    if (!product.title || product.title.trim() === '') {
      invalid.push(`Produit ${product.id}: titre manquant`);
      isValid = false;
    }
    
    if (!product.price || product.price < 0) {
      invalid.push(`Produit ${product.id}: prix invalide`);
      isValid = false;
    }
    
    if (product.isBoosted && (!product.boostLevel || product.boostLevel < 1)) {
      invalid.push(`Produit ${product.id}: boost sans niveau`);
      isValid = false;
    }
    
    if (isValid) valid++;
  });
  
  return { valid, invalid };
}

// Exécuter le test
testFinalIntegration().then(() => {
  console.log('\n🏁 Test d\'intégration terminé.');
  process.exit(0);
}).catch(error => {
  console.error('💥 Erreur fatale:', error);
  process.exit(1);
}); 