import {useCallback, useEffect, useState} from 'react';

const useSSE = (url) => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [eventSource, setEventSource] = useState(null);

    const createEventSource = useCallback(() => {
        let retryCount = 0;
        const maxRetries = 5;

        const connect = () => {
            const es = new EventSource(url);

            es.onmessage = (event) => {
                if (event.data !== 'ping') {
                    try {
                        const parsedData = JSON.parse(event.data);
                        setData(parsedData);
                    } catch (err) {
                        console.error('Error parsing SSE data:', err);
                        setError(err);
                    }
                }
            };

            es.onerror = (error) => {
                console.error('SSE error:', error);
                setError(error);
                es.close();

                if (retryCount < maxRetries) {
                    retryCount++;
                    console.log(`Retrying connection (${retryCount}/${maxRetries})...`);
                    setTimeout(connect, 5000 * retryCount); // Exponential backoff
                } else {
                    console.error('Max retries reached. Stopping reconnection attempts.');
                }
            };

            es.onopen = () => {
                console.log('SSE connection opened');
                retryCount = 0; // Reset retry count on successful connection
                setError(null);
            };

            setEventSource(es);
        };

        connect();
    }, [url]);

    useEffect(() => {
        createEventSource();

        return () => {
            if (eventSource) {
                console.log('Closing SSE connection');
                eventSource.close();
            }
        };
    }, [url, createEventSource]);

    return {data, error, eventSource};
};

export default useSSE;