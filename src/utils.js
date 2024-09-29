export const formatDate = (dateArray) => {
    // Destructure the array
    const [year, month, day, hour, minute, second, nanosecond] = dateArray;

    // Create a new Date object
    // Note: we subtract 1 from the month to account for JavaScript's 0-indexing
    const date = new Date(year, month - 1, day, hour, minute, second);

    // Add milliseconds (nanoseconds / 1,000,000)
    date.setMilliseconds(nanosecond / 1000000);

    // Format the date as ISO string and return only the part we need
    return date.toLocaleString().slice(0, 20).replace('T', ' ');
};