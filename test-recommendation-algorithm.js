const axios = require('axios');

// Configuration
const BASE_URL = 'http://localhost:8080/api';
const TEST_USER_ID = 1;

// Données de test pour les produits
const testProducts = [
  {
    id: 1,
    title: "iPhone 13 Pro Max - Excellent état",
    brand: "Apple",
    category: "electronics",
    condition: "excellent",
    price: 899.99,
    city: "Paris",
    favoriteCount: 15,
    viewCount: 120,
    isBoosted: true,
    boostLevel: 2
  },
  {
    id: 2,
    title: "Nike Air Max 270 - Taille 42",
    brand: "Nike",
    category: "fashion",
    condition: "good",
    price: 89.99,
    city: "Lyon",
    favoriteCount: 8,
    viewCount: 45,
    isBoosted: false,
    boostLevel: 0
  },
  {
    id: 3,
    title: "MacBook Pro M1 13 pouces",
    brand: "Apple",
    category: "electronics",
    condition: "excellent",
    price: 1299.99,
    city: "Paris",
    favoriteCount: 25,
    viewCount: 200,
    isBoosted: true,
    boostLevel: 3
  },
  {
    id: 4,
    title: "Adidas Ultraboost 21",
    brand: "Adidas",
    category: "fashion",
    condition: "new",
    price: 149.99,
    city: "Marseille",
    favoriteCount: 12,
    viewCount: 78,
    isBoosted: true,
    boostLevel: 1
  },
  {
    id: 5,
    title: "Samsung Galaxy S21",
    brand: "Samsung",
    category: "electronics",
    condition: "good",
    price: 599.99,
    city: "Toulouse",
    favoriteCount: 6,
    viewCount: 35,
    isBoosted: false,
    boostLevel: 0
  },
  {
    id: 6,
    title: "Levi's 501 Jeans",
    brand: "Levi's",
    category: "fashion",
    condition: "good",
    price: 49.99,
    city: "Bordeaux",
    favoriteCount: 4,
    viewCount: 22,
    isBoosted: false,
    boostLevel: 0
  },
  {
    id: 7,
    title: "Sony WH-1000XM4",
    brand: "Sony",
    category: "electronics",
    condition: "excellent",
    price: 299.99,
    city: "Paris",
    favoriteCount: 18,
    viewCount: 95,
    isBoosted: true,
    boostLevel: 2
  },
  {
    id: 8,
    title: "Zara Blazer Femme",
    brand: "Zara",
    category: "fashion",
    condition: "new",
    price: 79.99,
    city: "Lyon",
    favoriteCount: 3,
    viewCount: 18,
    isBoosted: false,
    boostLevel: 0
  }
];

// Données de test pour les interactions utilisateur
const testInteractions = [
  { productId: 1, type: "VIEW", userId: TEST_USER_ID },
  { productId: 1, type: "FAVORITE", userId: TEST_USER_ID },
  { productId: 3, type: "VIEW", userId: TEST_USER_ID },
  { productId: 3, type: "FAVORITE", userId: TEST_USER_ID },
  { productId: 7, type: "VIEW", userId: TEST_USER_ID },
  { productId: 2, type: "VIEW", userId: TEST_USER_ID },
  { productId: 4, type: "VIEW", userId: TEST_USER_ID },
  { productId: 4, type: "FAVORITE", userId: TEST_USER_ID }
];

// Fonction pour tester les recommandations
async function testRecommendations() {
  console.log('🚀 Test de l\'algorithme de recommandation avec produits boostés\n');
  
  try {
    // Test 1: Recommandations content-based
    console.log('📊 Test 1: Recommandations Content-Based');
    const contentBasedRecs = await axios.get(`${BASE_URL}/recommendations/for-me?type=content&limit=5`);
    console.log('✅ Content-Based:', contentBasedRecs.data.length, 'produits recommandés');
    displayRecommendations(contentBasedRecs.data, 'Content-Based');
    
    // Test 2: Recommandations collaboratives
    console.log('\n📊 Test 2: Recommandations Collaboratives');
    const collaborativeRecs = await axios.get(`${BASE_URL}/recommendations/for-me?type=collaborative&limit=5`);
    console.log('✅ Collaborative:', collaborativeRecs.data.length, 'produits recommandés');
    displayRecommendations(collaborativeRecs.data, 'Collaborative');
    
    // Test 3: Recommandations hybrides
    console.log('\n📊 Test 3: Recommandations Hybrides');
    const hybridRecs = await axios.get(`${BASE_URL}/recommendations/for-me?type=hybrid&limit=5`);
    console.log('✅ Hybrid:', hybridRecs.data.length, 'produits recommandés');
    displayRecommendations(hybridRecs.data, 'Hybrid');
    
    // Test 4: Analyse des boosts
    console.log('\n📊 Test 4: Analyse des Produits Boostés');
    await analyzeBoosts();
    
    // Test 5: Performance des algorithmes
    console.log('\n📊 Test 5: Performance des Algorithmes');
    await testAlgorithmPerformance();
    
  } catch (error) {
    console.error('❌ Erreur lors du test:', error.message);
    if (error.response) {
      console.error('Détails:', error.response.data);
    }
  }
}

// Fonction pour afficher les recommandations
function displayRecommendations(products, algorithmType) {
  console.log(`\n📋 Détails des recommandations ${algorithmType}:`);
  products.forEach((product, index) => {
    const boostInfo = product.isBoosted ? `🔥 Boosté (niveau ${product.boostLevel})` : '📦 Standard';
    console.log(`${index + 1}. ${product.title}`);
    console.log(`   💰 Prix: ${product.price}€ | ⭐ Favoris: ${product.favoriteCount} | 👁️ Vues: ${product.viewCount}`);
    console.log(`   🏷️ ${product.brand} | 📍 ${product.city} | ${boostInfo}\n`);
  });
}

// Fonction pour analyser les boosts
async function analyzeBoosts() {
  try {
    const boostedProducts = testProducts.filter(p => p.isBoosted);
    const regularProducts = testProducts.filter(p => !p.isBoosted);
    
    console.log(`🔥 Produits boostés: ${boostedProducts.length}/${testProducts.length}`);
    boostedProducts.forEach(product => {
      console.log(`   - ${product.title} (Boost niveau ${product.boostLevel})`);
    });
    
    console.log(`\n📊 Statistiques des boosts:`);
    const avgBoostLevel = boostedProducts.reduce((sum, p) => sum + p.boostLevel, 0) / boostedProducts.length;
    const avgFavoritesBoosted = boostedProducts.reduce((sum, p) => sum + p.favoriteCount, 0) / boostedProducts.length;
    const avgFavoritesRegular = regularProducts.reduce((sum, p) => sum + p.favoriteCount, 0) / regularProducts.length;
    
    console.log(`   📈 Niveau de boost moyen: ${avgBoostLevel.toFixed(1)}`);
    console.log(`   ❤️ Favoris moyens (boostés): ${avgFavoritesBoosted.toFixed(1)}`);
    console.log(`   ❤️ Favoris moyens (standards): ${avgFavoritesRegular.toFixed(1)}`);
    console.log(`   📊 Différence: +${(avgFavoritesBoosted - avgFavoritesRegular).toFixed(1)} favoris`);
    
  } catch (error) {
    console.error('❌ Erreur analyse boosts:', error.message);
  }
}

// Fonction pour tester la performance des algorithmes
async function testAlgorithmPerformance() {
  try {
    const algorithms = ['content', 'collaborative', 'hybrid'];
    const results = {};
    
    for (const algo of algorithms) {
      const startTime = Date.now();
      const response = await axios.get(`${BASE_URL}/recommendations/for-me?type=${algo}&limit=10`);
      const endTime = Date.now();
      
      results[algo] = {
        responseTime: endTime - startTime,
        productCount: response.data.length,
        boostedCount: response.data.filter(p => p.isBoosted).length
      };
    }
    
    console.log('⏱️ Temps de réponse:');
    Object.entries(results).forEach(([algo, data]) => {
      console.log(`   ${algo}: ${data.responseTime}ms (${data.productCount} produits, ${data.boostedCount} boostés)`);
    });
    
    // Trouver l'algorithme le plus rapide
    const fastest = Object.entries(results).reduce((a, b) => 
      results[a[0]].responseTime < results[b[0]].responseTime ? a : b
    );
    console.log(`\n🏆 Plus rapide: ${fastest[0]} (${results[fastest[0]].responseTime}ms)`);
    
  } catch (error) {
    console.error('❌ Erreur test performance:', error.message);
  }
}

// Fonction pour simuler des interactions utilisateur
async function simulateUserInteractions() {
  console.log('\n🎭 Simulation d\'interactions utilisateur...');
  
  try {
    for (const interaction of testInteractions) {
      await axios.post(`${BASE_URL}/interactions`, interaction);
      console.log(`   ✅ ${interaction.type} sur produit ${interaction.productId}`);
    }
    console.log('✅ Interactions simulées avec succès');
  } catch (error) {
    console.error('❌ Erreur simulation interactions:', error.message);
  }
}

// Fonction pour créer des produits boostés
async function createBoostedProducts() {
  console.log('\n🚀 Création de produits boostés...');
  
  try {
    for (const product of testProducts) {
      if (product.isBoosted) {
        await axios.post(`${BASE_URL}/products/${product.id}/boost`, {
          boostLevel: product.boostLevel,
          duration: 7 // jours
        });
        console.log(`   ✅ Produit ${product.id} boosté (niveau ${product.boostLevel})`);
      }
    }
    console.log('✅ Produits boostés créés avec succès');
  } catch (error) {
    console.error('❌ Erreur création boosts:', error.message);
  }
}

// Fonction principale
async function main() {
  console.log('🧪 Test de l\'algorithme de recommandation Souqly\n');
  
  // Vérifier la connexion
  try {
    await axios.get(`${BASE_URL}/health`);
    console.log('✅ Serveur connecté');
  } catch (error) {
    console.error('❌ Impossible de se connecter au serveur');
    return;
  }
  
  // Simuler les données
  await simulateUserInteractions();
  await createBoostedProducts();
  
  // Tester les recommandations
  await testRecommendations();
  
  console.log('\n🎉 Tests terminés !');
}

// Exécuter les tests
if (require.main === module) {
  main().catch(console.error);
}

module.exports = {
  testRecommendations,
  simulateUserInteractions,
  createBoostedProducts,
  analyzeBoosts
}; 