import {useCallback, useEffect, useState} from "react";
import {ssePoint} from "../api";
import {SseType} from "../constants";

const useSSE = () => {
    const [gameData, setGameData] = useState(null);
    const [scoreData, setScoreData] = useState(null);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState(null);
    const [es, setEs] = useState(null);

    useEffect(() => {
        const connect = (userId) => {
            const eventSource = new EventSource(ssePoint + `/${userId}`);
            let retryCount = 0;
            const maxRetries = 5;

            eventSource.onmessage = (event) => {
                try {
                    const parsedData = JSON.parse(event.data);
                    if (parsedData[1]?.data === SseType.heartbeat.toLowerCase()) {
                        console.log('Heartbeat received');
                    } else if (parsedData.type === SseType.game) {
                        setGameData(parsedData);
                    } else if (parsedData.type === SseType.score) {
                        setScoreData(parsedData);
                    }
                } catch (err) {
                    console.error('Error parsing SSE data:', err);
                    setError(err);
                }
            };

            eventSource.onerror = (error) => {
                console.error('SSE error:', error);
                setError(error);
                eventSource.close();
                if (retryCount < maxRetries) {
                    retryCount++;
                    console.log(`Retrying connection (${retryCount}/${maxRetries})...`);
                    setTimeout(connect, 5000 * retryCount); // Exponential backoff
                } else {
                    console.error('Max retries reached. Stopping reconnection attempts.');
                }
            };

            eventSource.onopen = () => {
                console.log('SSE connection opened');
                retryCount = 0; // Reset retry count on successful connection
                setError(null);
            };

            setEs(eventSource);
        };

        if (userId && !es) {
            connect(userId);
        }

        return () => {
            if (es) {
                console.log('Closing SSE connection');
                es.close();
                setEs(null);
            }
        };
    }, [userId]);

    const closeEventSource = useCallback(() => {
        if (userId && es) {
            fetch(`${ssePoint}/close/${userId}`, {
                method: 'POST',
            }).catch(error => console.error('Error closing SSE connection:', error));
            es.close();
            setEs(null);
        }
    }, [userId]);

    return {gameData, scoreData, setUserId, error, closeEventSource};
};

export default useSSE;