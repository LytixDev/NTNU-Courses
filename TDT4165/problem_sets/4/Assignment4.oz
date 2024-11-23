declare Enumerate GenerateOdd ListDivisorsOf ListPrimesUntil EnumerateLazy Primes Take in

% Task 2
fun {Enumerate Start End} 
    thread
        if Start < End + 1 then
            Start|{Enumerate Start+1 End}
        else nil end
    end
end

fun {GenerateOdd Start End}
    thread
        if Start < End + 1 then
            if Start mod 2 == 1 then
                Start|{GenerateOdd Start+1 End}
            else
                {GenerateOdd Start+1 End}
            end
        else nil end
    end
end

{Browse {Enumerate 1 5}}
{Browse {GenerateOdd 1 5}}
{Browse {GenerateOdd 4 4}}

% Task 3

fun {ListDivisorsOf Number}
    Divisors = {Filter {Enumerate 1 Number} fun {$ Num} Number mod Num == 0 end}
in
    Divisors
end

% Use Enumerate to get every number, then use listofdevisors for every number
fun {ListPrimesUntil N}
    {Filter {Enumerate 1 N} fun {$ Num} {Length {ListDivisorsOf Num}} == 2 end}
end

{Browse {ListDivisorsOf 10}}
{Browse {ListPrimesUntil 100}}

% Task 4


fun {EnumerateLazy}
    local
        fun lazy {EnumerateLazyInternal Start}
            Start | {EnumerateLazyInternal Start + 1}
        end
    in
        {EnumerateLazyInternal 1}
    end
end

fun lazy {Primes}
    local
        fun lazy {Sieve Stream}
            % Based on the code from page 291 of the CTMP Book
            case Stream of H|T then
                H | {Sieve {Filter T fun {$ X} X mod H \= 0 end}}
            end
        end
    in
        {Sieve {EnumerateLazy}}
    end
end