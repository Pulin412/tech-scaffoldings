import http from 'k6/http';
import { check } from 'k6';

/**
 * Authenticate using OAuth against any Active Directory
 * @function
 * @returns {object} - The authentication response.
 */
export function authenticate() {
    if (__ENV.K6_ENV !== 'local') {
        const requestBody = {
            grant_type: __ENV.GRANT_TYPE,
            client_id: __ENV.AAD_CLIENT_ID,
            client_secret: __ENV.AAD_CLIENT_SECRET,
            password: __ENV.AAD_PASSWORD,
            username: __ENV.AAD_USERNAME,
        };

        const headers = {
            headers: {
                'Content-type': 'application/x-www-form-urlencoded',
            },
        };

        const response = http.post(baseUrl, requestBody, headers);

        // Verify response
        check(response, {
            'status is 200': (r) => r.status === 200,
            'Access token generated': (r) => r.json().access_token != null,
        });

        return response.json();
    }

    return {
        access_token: __ENV.LOCAL_ACCESS_TOKEN,
    };
}
