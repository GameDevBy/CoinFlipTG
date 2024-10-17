import { useEffect, useState } from 'react';

const useSSE = (url) => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const eventSource = new EventSource(url);

        eventSource.onmessage = (event) => {
            const parsedData = JSON.parse(event.data);
            setData(parsedData);
        };

        eventSource.onerror = (error) => {
            console.error('SSE error:', error);
            setError(error);
            eventSource.close();
        };

        return () => {
            eventSource.close();
        };
    }, [url]);

    return { data, error };
};

export default useSSE;