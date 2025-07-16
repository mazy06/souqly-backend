const axios = require('axios');

const BASE_URL = 'http://localhost:8080/api';

async function testReviews() {
  console.log('🧪 Test des endpoints de commentaires...\n');

  try {
    // Test 1: Récupérer la note moyenne d'un vendeur
    console.log('1. Test récupération note moyenne vendeur ID 1:');
    const ratingResponse = await axios.get(`${BASE_URL}/reviews/seller/1/rating`);
    console.log('✅ Succès:', JSON.stringify(ratingResponse.data, null, 2));
    console.log(`   - Note moyenne: ${ratingResponse.data.averageRating}`);
    console.log(`   - Nombre total d'avis: ${ratingResponse.data.totalReviews}`);
    console.log(`   - Commentaires récents: ${ratingResponse.data.recentReviews.length}\n`);

    // Test 2: Récupérer tous les commentaires d'un vendeur
    console.log('2. Test récupération tous les commentaires vendeur ID 1:');
    const reviewsResponse = await axios.get(`${BASE_URL}/reviews/seller/1`);
    console.log('✅ Succès:', JSON.stringify(reviewsResponse.data, null, 2));
    console.log(`   - Nombre de commentaires: ${reviewsResponse.data.length}\n`);

    // Test 3: Créer un nouveau commentaire
    console.log('3. Test création nouveau commentaire:');
    const newReview = {
      productId: 2,
      sellerId: 1,
      buyerId: 2,
      rating: 4,
      comment: "Vendeur très professionnel, livraison rapide !",
      transactionId: "test-456"
    };
    const createResponse = await axios.post(`${BASE_URL}/reviews`, newReview);
    console.log('✅ Succès:', JSON.stringify(createResponse.data, null, 2));
    console.log(`   - Commentaire créé avec ID: ${createResponse.data.id}\n`);

    // Test 4: Vérifier que la note moyenne a été mise à jour
    console.log('4. Test vérification mise à jour note moyenne:');
    const updatedRatingResponse = await axios.get(`${BASE_URL}/reviews/seller/1/rating`);
    console.log('✅ Succès:', JSON.stringify(updatedRatingResponse.data, null, 2));
    console.log(`   - Nouvelle note moyenne: ${updatedRatingResponse.data.averageRating}`);
    console.log(`   - Nouveau nombre total d'avis: ${updatedRatingResponse.data.totalReviews}\n`);

    console.log('🎉 Tous les tests sont passés avec succès !');
    console.log('\n📊 Résumé:');
    console.log(`   - Note moyenne du vendeur 1: ${updatedRatingResponse.data.averageRating.toFixed(1)}/5`);
    console.log(`   - Nombre total d'avis: ${updatedRatingResponse.data.totalReviews}`);
    console.log(`   - Commentaires récents disponibles: ${updatedRatingResponse.data.recentReviews.length}`);

  } catch (error) {
    console.error('❌ Erreur lors des tests:', error.response?.data || error.message);
  }
}

testReviews(); 