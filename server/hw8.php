<?php
/**
 * Created by PhpStorm.
 * User: zkc
 * Date: 4/4/18
 * Time: 12:26 AM
 * Yelp Fusion API code sample.*/
header('Access-Control-Allow-Methods: GET, POST');
header("Access-Control-Allow-Headers: X-Requested-With");
if(isset($_GET['keyword'])){
    $GOOGLE_PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    $GOOGLE_PLACE_ACCESS_KEY="AIzaSyD2zAN0zVVruaKEDRtVbweZwf586lCjM7Y";
    $radius = $_GET['radius'] * 1609.34;

    $GOOGLE_PLACE_URL .= "location=" .$_GET['location'] ."&radius=" .$radius ."&type=" .$_GET['type'];
    $GOOGLE_PLACE_URL .= "&keyword=" .$_GET['keyword'] ."&key=" .$GOOGLE_PLACE_ACCESS_KEY;
    $GOOGLE_PLACE_URL .= "&pagetoken=" .$_GET['pagetoken'];
    $GOOGLE_PLACE_URL = str_replace(' ','%20', $GOOGLE_PLACE_URL);
    echo file_get_contents($GOOGLE_PLACE_URL);
}
if(isset($_GET['yelp'])){
    /**
     * Created by PhpStorm.
     * User: zkc
     * Date: 4/4/18
     * Time: 12:26 AM
     * Yelp Fusion API code sample.
     *
     * This program demonstrates the capability of the Yelp Fusion API
     * by using the Business Search API to query for businesses by a
     * search term and location, and the Business API to query additional
     * information about the top result from the search query.
     *
     * Please refer to http://www.yelp.com/developers/v3/documentation
     * for the API documentation.
     *
     * Sample usage of the program:
     * `php sample.php --term="dinner" --location="San Francisco, CA"`
     */
// API key placeholders that must be filled in by users.
// You can find it on
// https://www.yelp.com/developers/v3/manage_app
    $API_KEY = "NhlOW3I6Ku0SwPTQghC91DX8dpovOUKR2NmXNBp-Cqw7ktI3tM33cNu7O2Rk81K7UetzBj6Z2Efuo1ozKkNOzj6EeVoVozIsqmeYbz9DO0wVGEg_XsEQl7qn9XfEWnYx";
// Complain if credentials haven't been filled out.
//assert($API_KEY, "Please supply your API key.");
// API constants, you shouldn't have to change these.
    $API_HOST = "https://api.yelp.com";
    $SEARCH_PATH = "/v3/businesses/search";
    $BUSINESS_PATH = "/v3/businesses/";  // Business ID will come after slash.
    $REVIEW_PATH = "/reviews";
// Defaults for our simple example.
    $DEFAULT_TERM = "default";
    $DEFAULT_LOCATION = "S Main St, Los Angeles";
    $SEARCH_LIMIT = 3;
    /**
     * Makes a request to the Yelp API and returns the response
     *
     * @param    $host    The domain host of the API
     * @param    $path    The path of the API after the domain.
     * @param    $url_params    Array of query-string parameters.
     * @return   The JSON response from the request
     */
    function request($host, $path, $url_params = array()) {
        // Send Yelp API Call
        try {
            $curl = curl_init();
            if (FALSE === $curl) {
                throw new Exception('Failed to initialize');
            }
            $url = $host . $path . "?" . http_build_query($url_params);
            curl_setopt_array($curl, array(
                CURLOPT_URL => $url,
                CURLOPT_RETURNTRANSFER => true,  // Capture response.
                CURLOPT_ENCODING => "",  // Accept gzip/deflate/whatever.
                CURLOPT_MAXREDIRS => 10,
                CURLOPT_TIMEOUT => 30,
                CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
                CURLOPT_CUSTOMREQUEST => "GET",
                CURLOPT_HTTPHEADER => array(
                    "authorization: Bearer " . $GLOBALS['API_KEY'],
                    "cache-control: no-cache",
                ),
            ));
            $response = curl_exec($curl);
            if (FALSE === $response) {
                throw new Exception(curl_error($curl), curl_errno($curl));
            }
            $http_status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
            if (200 != $http_status) {
                throw new Exception($response, $http_status);
            }
            curl_close($curl);
        } catch(Exception $e) {
            trigger_error(sprintf(
                'Curl failed with error #%d: %s',
                $e->getCode(), $e->getMessage()),
                E_USER_ERROR);
        }
        return $response;
    }
    /**
     * Query the Search API by a search term and location
     *
     * @param    $term        The search term passed to the API
     * @param    $location    The search location passed to the API
     * @return   The JSON response from the request
     */
    function search($term, $location) {
        $url_params = array();

        $url_params['term'] = $term;
        $url_params['location'] = $location;
        $url_params['limit'] = $GLOBALS['SEARCH_LIMIT'];

        return request($GLOBALS['API_HOST'], $GLOBALS['SEARCH_PATH'], $url_params);
    }
    /**
     * Query the Business API by business_id
     *
     * @param    $business_id    The ID of the business to query
     * @return   The JSON response from the request
     */
    function get_business($business_id) {
        $business_path = $GLOBALS['BUSINESS_PATH'] . urlencode($business_id) .$GLOBALS['REVIEW_PATH'];

        return request($GLOBALS['API_HOST'], $business_path);
    }
    /**
     * Queries the API by the input values from the user
     *
     * @param    $term        The search term to query
     * @param    $location    The location of the business to query
     */
    function query_api($term, $location) {
        $response = json_decode(search($term, $location));
        $business_id = $response->businesses[0]->id;
        /*print sprintf(
            "%d businesses found, querying business info for the top result \"%s\"\n\n",
            count($response->businesses),
            $business_id
        );*/
        $response = get_business($business_id);

        //print sprintf("Result for business \"%s\" found:\n", $business_id);
        $pretty_response = json_encode(json_decode($response), JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);
        echo $pretty_response;
    }
    /**
     * User input is handled here
     */
    $longopts  = array(
        "term::",
        "location::",
    );

    $options = getopt("", $longopts);
    $term = $options['term'] ?: $GLOBALS['DEFAULT_TERM'];
    $location = $_GET['location'] ?: $GLOBALS['DEFAULT_LOCATION'];
    query_api($term, $location);
}
?>